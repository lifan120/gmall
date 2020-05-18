package com.atguigu.gmall.pay.test1;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Myconsumer1
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-24
 * @Description:
 */
public class Myconsumer1 {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,ActiveMQConnectionFactory.DEFAULT_PASSWORD,"tcp://localhost:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("testMessageForTopic");
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if(message instanceof TextMessage){
                        String text = null;
                        try {
                            text = ((TextMessage) message).getText();
                            System.out.println(text);
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}