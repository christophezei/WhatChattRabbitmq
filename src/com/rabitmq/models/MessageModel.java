package com.rabitmq.models;

public class MessageModel {
	private String messageSender;
	private String messageReceiver;
	private String messageBody;
	private String sentTime;
	private Enum messageType;
	/*private DateTimeFormatter dateF = DateTimeFormatter.ofPattern("HH:mm:ss");
	LocalDateTime now = LocalDateTime.now();*/
	
	public String getSentTime() {
		return sentTime;
	}
	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}
	public Enum getMessageType() {
		return messageType;
	}
	public void setMessageType(Enum messageType) {
		this.messageType = messageType;
	}
	public String getMessageReceiver() {
		return messageReceiver;
	}
	public void setMessageReceiver(String messageReceiver) {
		this.messageReceiver = messageReceiver;
	}
	
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	
	public String getMessageSender() {
		return messageSender;
	}
	public void setMessageSender(String messageSender) {
		this.messageSender = messageSender;
	}
}
