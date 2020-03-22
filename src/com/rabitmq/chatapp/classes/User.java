package com.rabitmq.chatapp.classes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabitmq.interfaces.IUser;

public class User implements IUser {

	@Override
	public ConnectionFactory connectToServer(ConnectionFactory factory){
		 factory = new ConnectionFactory();
	     factory.setHost("localhost");
	     return factory;		
	}

	@Override
	public synchronized void produceMessage(String exchangeName, String message, String exchangeType, ConnectionFactory factory) throws IOException, TimeoutException {
		try (Connection connection = factory.newConnection();
	            Channel channel = connection.createChannel()) {
			    channel.exchangeDeclare(exchangeName, exchangeType);
		        channel.basicPublish(exchangeName,"", null, message.getBytes(StandardCharsets.UTF_8));
	        }
		
	}
	
	@Override
	public  void consumeMessage(ConnectionFactory factory,String exchangeName, String exchangeType) throws IOException, TimeoutException {
		
		Connection connection =  factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, exchangeType);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "");
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(message);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		
	}

}
