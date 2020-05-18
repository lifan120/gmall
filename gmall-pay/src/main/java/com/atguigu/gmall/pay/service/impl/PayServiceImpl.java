package com.atguigu.gmall.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.pay.mapper.PaymentInfoMapper;
import com.atguigu.gmall.service.PayService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * PayServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-23
 * @Description:
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;
    @Autowired
    AlipayClient alipayClient;
    @Override
    public void savePayMentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayMentInfo(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn",paymentInfo.getOrderSn());
        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);
    }

    @Override
    public void checkPayStatusMq(String out_trade_no,int count) {
        //发送一个延迟队列消息，目的是为了检查消费状态
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(true,Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAY_CHECK_QUEUE");
            MessageProducer messageProducer = session.createProducer(queue);
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("out_trade_no",out_trade_no);
            mapMessage.setInt("count",count);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*20);
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            messageProducer.send(mapMessage);
            session.commit();
            messageProducer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    //查看db检查用户是否支付成功
    @Override
    public PaymentInfo checkPayStatus(String out_trade_no) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
            paymentInfoMapper.selectOne(paymentInfo);
        return paymentInfo;
    }

    @Override
    public PaymentInfo checkPayStatusForAli(String out_trade_no) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map map = new HashMap();
        map.put("out_trade_no",out_trade_no);
        request.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
        if(response.isSuccess()){
            String tradeStatus = response.getTradeStatus();
            //判断用户是否支付成功，如果支付成功
            if(response.getTradeStatus().equals("TRADE_SUCCESS")){
                String tradeNo = response.getTradeNo();
                String outTradeNo = response.getOutTradeNo();
                String callbackContent = response.getBody();
                paymentInfo.setPaymentStatus("1");
                paymentInfo.setCallbackContent(callbackContent);
                paymentInfo.setCallbackTime(new Date());
                paymentInfo.setAlipayTradeNo(tradeNo);
            }
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        return paymentInfo;
    }

    @Override
    public void sendPaySuccessMq(PaymentInfo paymentInfo) {
        //发送一条支付成功的消息
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAY_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = session.createMapMessage();
            mapMessage.setString("out_trade_no",paymentInfo.getOrderSn());
            mapMessage.setString("pay_status",paymentInfo.getPaymentStatus());
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);
            session.commit();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
