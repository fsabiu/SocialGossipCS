package client;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChatroomListener extends Thread {
	//Chatroom Listener configuration
	private int listeningPort; //Listening port
	private MulticastSocket socket; //Chatroom multicast socket
	private InetAddress msAddress; //Chatroom address
		
	public ChatroomListener() {
		
	}
}
