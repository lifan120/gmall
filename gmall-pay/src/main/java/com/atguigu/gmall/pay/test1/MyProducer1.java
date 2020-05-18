package com.atguigu.gmall.pay.test1;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

/**
 * MyProducer1
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-24
 * @Description:
 */
public class MyProducer1 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        try {
            Connection connection = connectionFactory.createConnection();//获得连接
            connection.start();//开启连接
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);//创建事务开启的会话
            //创建队列
            Topic topic = session.createTopic("testMessageForTopic");
            //队列生产消息者,通过它发送消息
            MessageProducer producer = session.createProducer(topic);
            //消息内容
            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText("I want make love too");
            //设置数据为持久化，服务器拓机重启也存在
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
