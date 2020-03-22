package com.rabitmq.chatapp.classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

	@Override
	public synchronized void writeToFile(String message) {
		String userHome = createDirectory();
		File log = new File(userHome + "/History.txt");
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(log, true));
			writer.println(message);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	@Override
	public String retreiveHistory(String absolutePath) {
		String history = null;
		try {
			history = new String(Files.readAllBytes(Paths.get(absolutePath)), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// can print any error
		}
		return history;
	}
	
	private String createDirectory() {
		boolean success = false;
		String dir = System.getProperty("user.home");
		String fullPath = dir + "/WhatChatMqHistory";
		File directory = new File(fullPath);
		if (directory.exists() && directory.isDirectory()) {
			//System.out.println("Directory already exists ...");

		} else {
			//System.out.println("Directory not exists, creating now");
			success = directory.mkdir();
		}
		return fullPath;
	}

}
