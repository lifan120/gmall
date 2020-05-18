package com.atguigu.gmall.pay.mq;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.service.PayService;
import com.atguigu.gmall.util.ActiveMQUtil;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * PayConsumer
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-25
 * @Description:
 */
@Component
public class PayConsumer {
    @Autowired
    PayService payService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_CHECK_QUEUE")
    public void cosumePayCheckQueue(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        String out_trade_no = activeMQMapMessage.getString("out_trade_no");
        int count = activeMQMapMessage.getInt("count");
        //根据外部订单号调用支付宝接口查询是否支付成功
        PaymentInfo paymentInfoForAli = payService.checkPayStatusForAli(out_trade_no);
        String paymentStatus = paymentInfoForAli.getPaymentStatus();
        if(StringUtils.isNotBlank(paymentStatus)&&paymentStatus.equals("1")) {
            //进行幂等性检查，如果已经提交了就不重复性修改了
            PaymentInfo paymentInfoForDb = payService.checkPayStatus(out_trade_no);
            if(!StringUtils.isNotBlank(paymentInfoForAli.getPaymentStatus())&&!paymentInfoForAli.getPaymentStatus().equals("1")) {
                payService.updatePayMentInfo(paymentInfoForAli);
                //更新数据库支付信息，发送支付成功消息队列
                payService.sendPaySuccessMq(paymentInfoForAli);
            }

        }

        if(count>0){//未支付并且检查次数还剩余
            System.out.println("开始第"+count+"次检查");
            count--;
            payService.checkPayStatusMq(out_trade_no,count);
        }else{
            //检查次数已终结，不再检查并进行相应操作
            System.out.println("检查次数已终结，不再检查并进行相应操作");
        }

    }

}
