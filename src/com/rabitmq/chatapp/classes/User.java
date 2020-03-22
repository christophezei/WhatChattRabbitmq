package com.rabitmq.chatapp.classes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabitmq.chatapp.classes.Client.MessageType;
import com.rabitmq.interfaces.IUser;
import com.rabitmq.models.MessageModel;

public class User implements IUser {

	@Override
	public ConnectionFactory connectToServer(ConnectionFactory factory){
		 factory = new ConnectionFactory();
	     factory.setHost("localhost");
	     return factory;		
	}

	@Override
	public synchronized void produceMessage(String exchangeName, MessageModel message, String exchangeType, ConnectionFactory factory) throws IOException, TimeoutException {
		try (Connection connection = factory.newConnection();
	            Channel channel = connection.createChannel()) {
			    channel.exchangeDeclare(exchangeName, exchangeType);
			    String messageContent;
			    String routingKey = null;
			    MessageType messageType = MessageType.NONE;
			    if(message.getMessageType() == messageType) {
		    		 routingKey = "NONE";
			    	 messageContent = message.getMessageSender() + message.getMessageBody();
			    	 channel.basicPublish(exchangeName,routingKey, null, messageContent.getBytes(StandardCharsets.UTF_8));
			    }else {
			    	
			    	  if(message.getMessageType() == MessageType.BROADCAST) {
			    		   routingKey = message.getMessageReceiver();
			    	  }else if(message.getMessageType() == MessageType.PRIVATE) {
			    		  	
			    		    messageContent = message.getMessageBody();
			    			String[] parts = messageContent.split(" ");
			    			routingKey = parts[0].substring(1);
			    			String messageBody = parts[1];
			    		    channel.basicPublish(exchangeName,routingKey, null, messageBody.getBytes(StandardCharsets.UTF_8));
			    	  }
			    	 
			    	  messageContent = "[" + message.getSentTime()+ "]" + message.getMessageSender() + ":" + message.getMessageBody(); 
			    	  channel.basicPublish(exchangeName,routingKey, null, messageContent.getBytes(StandardCharsets.UTF_8));
			    }
		       
	        }
		
	}
	
	@Override
	public  void consumeMessage(ConnectionFactory factory,String exchangeName, String exchangeType,MessageModel message) throws IOException, TimeoutException {
		
		Connection connection =  factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, exchangeType);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, "BROADCAST");
        channel.queueBind(queueName, exchangeName,"NONE");
  
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageBody = new String(delivery.getBody(), "UTF-8");
            System.out.println(messageBody);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		
	}

	@Override
	public void initMyOwenChannelBindingForPrivComunucation(ConnectionFactory factory,String exchangeName, String exchangeType,MessageModel message) throws IOException, TimeoutException  {
		Connection connection =  factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchangeName, exchangeType);
        String queueName = channel.queueDeclare().getQueue();
 
        channel.queueBind(queueName, exchangeName, message.getMessageSender());
        
        
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String messageBody = new String(delivery.getBody(), "UTF-8");
            System.out.println(messageBody);
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
		
	}

}
