package com.rabitmq.interfaces;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;

public interface IUser {
	ConnectionFactory connectToServer(ConnectionFactory factory);
	void  produceMessage(String exchangeName, String message, String exchangeType, ConnectionFactory factory) throws IOException, TimeoutException ;
	void consumeMessage(ConnectionFactory factory,String exchangeName,String exchangeType)throws IOException, TimeoutException;
}
