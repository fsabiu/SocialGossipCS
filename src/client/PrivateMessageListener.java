package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

import communication.RequestMessage;

public class PrivateMessageListener extends Thread{
	 ObjectInputStream message_in = null;
	 ConcurrentHashMap<String, GUI> interfaces = null;
	 
	 public PrivateMessageListener(ConcurrentHashMap<String, GUI> interfaces, ObjectInputStream message_in) {
	  this.interfaces = interfaces;
	  this.message_in = message_in;
	 }
	 
	 public void run() {
		 while(true) {
			RequestMessage msg = null;
			msg = receiveMessage();
			System.out.println("Il messaggio è "+msg);
			for(String s : interfaces.keySet()) {
				System.out.println(s);
			}
			//String receiver = (String) msg.getParameter("RECEIVER");
			String sender = (String) msg.getParameter("SENDER");
			ChatGUI chatGUI = (ChatGUI) interfaces.get("chatGUI"+sender);
			
			chatGUI.setConversationArea("["+sender+"] "+msg.getParameter("BODY"));
		 }
	 }
	 
	 public RequestMessage receiveMessage() {
		 RequestMessage msg = null;
		 try {
			 msg=(RequestMessage) message_in.readObject();
		 } catch (ClassNotFoundException | IOException e) {
			 e.printStackTrace();
		 }
		 return msg;
	 }
}
