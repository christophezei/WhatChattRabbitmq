# Christophe El Zeinaty 
# Youhana Mikhaiel

# WhatChat RabbitMq
Welcome to Whatchat, this a java console chatting application based on rabbitmq technology.
It contains the following features :<br/> 
1)Global chat room<br/>
2)Private messaging<br/>
3)History of the global chat room<br/>
4)Chat persistency<br/>

# How to run the server ?
The server here is the rabbitmq server run on your local machine
   
# How to run the client ?
Simple navigate to the jar directory and run the following command:<br/>
   <b>java -jar client.jar  username </b>

# Main comunucation class function signature
<b>the main functions signatures:</b><br/>
   1)ConnectionFactory connectToServer(ConnectionFactory factory);<br/>
   2)void  produceMessage(String exchangeName, MessageModel message, String exchangeType, ConnectionFactory factory) throws IOException, TimeoutException ;<br/>
   3)void consumeMessage(ConnectionFactory factory,String exchangeName,String exchangeType, MessageModel message)throws IOException, TimeoutException;<br/>
   4)void initMyOwenChannelBindingForPrivComunucation(ConnectionFactory factory,String exchangeName, String exchangeType,MessageModel message) throws IOException, TimeoutException ;<br/>
   5)void writeToFile(String message) throws RemoteException;<br/>
   6)String retreiveHistory(String absolutePath);<br/>
   
# Design approach
<p>
    In this project we have a consumer/producer problem since we are using rabbitmq, by saying that we should keep in mind 
    that in this application a client is both the sender and receiver in most cases , we decided to use topics property in rabbit mq to be able to send a broadcast message to everyone and to be able to send private messages, for private messages our approach is the following each time a new client is registered his exchange will be bounded with his name (using initMyOwenChannelBindingForPrivComunucation method) so in this case its easy to send him a message privately by sending the rounting key which in this case will be the receiver name. For the history part what we did is letting each client write his own history in a text file where a directory is created in your 'home' dir on your pc, in this directory we will store public history of the global chat room under the textfile 'history.txt' we are aware that each client can be on a diffrent machine but we decided to save the history based on once his logged in for the first time we can also run a client and make him behave as a server for this purpose and for the private messages we consider them as temporary messages in this app so we dont store them. </p>
   
   # Useful commands you can use
   1)To see your history enter the following command '/history'<br/>
   2)To chat a specific user privately enter the following command '@usernameYouWantToChat <message>'<br/>
   3) To exit press CTRL+C chat room application will terminate<br/>
