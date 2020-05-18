package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.service.OrderService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

/**
 * OrderConsumer
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-25
 * @Description:
 */
@Component
public class OrderConsumer {
    @Reference
    OrderService orderService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_SUCCESS_QUEUE")
    public void consumePaySuccessQueue(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        String out_trade_no = activeMQMapMessage.getString("out_trade_no");
        String pay_status = activeMQMapMessage.getString("pay_status");
        //更新订单状态
        orderService.updateOrderByOutTradeNo(out_trade_no,pay_status);
        //发送一个消息队列通知库存系统发货
        /*orderService.sendUpdateOrderSuccessMq();*/
        //可以根据业务更新订单状态已通知仓库发货
    }
}
