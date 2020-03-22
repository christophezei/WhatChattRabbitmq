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
	private String EXCHANGE_NAME="topic_chat_";
	private User user;
	private MessageModel message;
	private Boolean isInPrivateChatMode = false;
	

	public enum MessageType{
		BROADCAST,
		PRIVATE,
		NONE
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
		MessageType messageType = MessageType.NONE;
		System.out.println(" Global Chat Room. To exit press CTRL+C");
		this.constructMessage(clientName,"NONE", messageType ,messageBody, dtf.format(now).toString());
		initBindingForPrivComunication();
		produceMessages();
		Scanner scanner = new Scanner(System.in);
		consumeMessages();
		while(true) {
			messageBody = scanner.nextLine();
			if(messageBody.charAt(0) == '@') {
				this.constructMessage(clientName,messageBody.substring(1), messageType.PRIVATE,messageBody, dtf.format(now).toString());
				produceMessages();
				
			}else {
				this.constructMessage(clientName,"BROADCAST", messageType.BROADCAST,messageBody, dtf.format(now).toString());
			    produceMessages();
			}
				
		}
	}


	private void consumeMessages() {
		try {
			user.consumeMessage(factory,EXCHANGE_NAME,"topic",message);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		}
	}


	private void produceMessages() {
		try {
			user.produceMessage(EXCHANGE_NAME,message,"topic", factory);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}


	private void initBindingForPrivComunication() {
		try {
			message.setMessageSender(clientName);
			user.initMyOwenChannelBindingForPrivComunucation(factory,EXCHANGE_NAME,"topic",message);
		} catch (IOException | TimeoutException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	private void constructMessage(String messageSender, String messageReceiver, Enum messageType, String messageBody, String sentTime) {
		message.setMessageSender(messageSender);
		message.setMessageReceiver(messageReceiver);
		message.setMessageType(messageType);
		message.setMessageBody(messageBody);
		message.setSentTime(sentTime);		
	}
	
	public Boolean getIsInPrivateChatMode() {
		return isInPrivateChatMode;
	}


	public void setIsInPrivateChatMode(Boolean isInPrivateChatMode) {
		this.isInPrivateChatMode = isInPrivateChatMode;
	}
}
