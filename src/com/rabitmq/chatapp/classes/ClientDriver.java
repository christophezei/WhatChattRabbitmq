package com.rabitmq.chatapp.classes;

import com.rabitmq.models.UserModel;

public class ClientDriver {

	public static void main(String[] args){
		UserModel user = new UserModel();
		user.setUserName(args[0]);
		new Thread(new Client(user)).start();
	}
}
