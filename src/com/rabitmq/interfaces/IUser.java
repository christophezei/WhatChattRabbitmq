package com.rabitmq.interfaces;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;
import com.rabitmq.models.MessageModel;

public interface IUser {
	ConnectionFactory connectToServer(ConnectionFactory factory);
	void  produceMessage(String exchangeName, MessageModel message, String exchangeType, ConnectionFactory factory) throws IOException, TimeoutException ;
	void consumeMessage(ConnectionFactory factory,String exchangeName,String exchangeType, MessageModel message)throws IOException, TimeoutException;
	void initMyOwenChannelBindingForPrivComunucation(ConnectionFactory factory,String exchangeName, String exchangeType,MessageModel message) throws IOException, TimeoutException ;
}
