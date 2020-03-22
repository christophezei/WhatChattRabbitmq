package com.rabitmq.chatapp.classes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabitmq.models.MessageModel;
import com.rabitmq.models.UserModel;

public class Client implements Runnable  {
	
	private ConnectionFactory factory;
	private String clientName = null;
	private String EXCHANGE_NAME="Exchange_Broadcast_Channe";
	private User user;
	private MessageModel message;
	private enum MessageType{
		BROADCAST,
		PRIVATE
	};
	
	protected Client(UserModel client) {
		this.clientName = client.getUserName();
		 user = new User();
		 message = new MessageModel();
		 factory = user.connectToServer(factory);
	}

	
	@Override
	public void run() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String messageBody;
		messageBody =" joined the chat room!";
		MessageType messageType = MessageType.BROADCAST;
		System.out.println(" Global Chat Room. To exit press CTRL+C");
		this.constructMessage(clientName,"everyone", messageType ,messageBody, dtf.format(now).toString());
		try {
			user.produceMessage(EXCHANGE_NAME,message.getMessageSender() + message.getMessageBody(),"fanout", factory);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(System.in);
		try {
			user.consumeMessage(factory,EXCHANGE_NAME,"fanout");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
		while(true) {
			messageBody = scanner.nextLine();
			this.constructMessage(clientName,"everyone", messageType.BROADCAST,messageBody, dtf.format(now).toString());
		    try {
				user.produceMessage(EXCHANGE_NAME,"[" + message.getSentTime()+ "]" + message.getMessageSender() + ":" + message.getMessageBody(),"fanout", factory);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
				
		}
	}
	
	private void constructMessage(String messageSender, String messageReceiver, Enum messageType, String messageBody, String sentTime) {
		message.setMessageSender(messageSender);
		message.setMessageReceiver(messageReceiver);
		message.setMessageType(messageType);
		message.setMessageBody(messageBody);
		message.setSentTime(sentTime);		
	}
}
