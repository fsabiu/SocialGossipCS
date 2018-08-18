package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateMessageListener extends Thread{
	 DataInputStream message_in = null;
	 ConcurrentHashMap<String, GUI> interfaces = null;
	 
	 public PrivateMessageListener(ConcurrentHashMap<String, GUI> interfaces, DataInputStream message_in) {
	  this.interfaces = interfaces;
	  this.message_in = message_in;
	 }
	 
	 public void run() {
	  while(true) {
		  String arrivato;
		  try {
			arrivato=message_in.readUTF();
			System.out.println("Il messaggio è "+arrivato);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	 }
}
