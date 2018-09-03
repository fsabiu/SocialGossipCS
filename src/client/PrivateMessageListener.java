package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.ConcurrentHashMap;

import communication.RequestMessage;

/**
 * PrivateMessageListener class. It is used to send private messages 
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
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
			String sender = (String) msg.getParameter("SENDER");
			ChatGUI chatGUI;
			if(!interfaces.containsKey("chatGUI"+sender)) {
				chatGUI = new ChatGUI(sender);
				chatGUI.setVisible(false);
				interfaces.putIfAbsent("chatGUI"+sender, chatGUI);
			} else {
				chatGUI = (ChatGUI) interfaces.get("chatGUI"+sender);
			}
			chatGUI.setConversationArea("["+sender+"] "+msg.getParameter("BODY"));
		 }
	 }
	 
	 public RequestMessage receiveMessage() {
		 RequestMessage msg = null;
		 try {
			 msg=(RequestMessage) message_in.readObject();
		 } catch (ClassNotFoundException | IOException e) {
			 System.out.println("Connection closed by server");
			 System.exit(-2);
		 }
		 return msg;
	 }
}
