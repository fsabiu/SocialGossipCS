package client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChatroomListener extends Thread {
	//Chatroom Listener configuration
	private MulticastSocket socket; //Chatroom multicast socket
	private InetAddress msAddress; //Chatroom address
	
	ChatroomGUI chatroomGUI;
	
	//Message buffer settings
	private static final int BUFFER_LEN = 1024;
		
	public ChatroomListener(ChatroomGUI chatroomGUI, String msname, String port) {
		try {
			this.chatroomGUI=chatroomGUI;
			this.socket = new MulticastSocket(Integer.parseInt(port));
			this.msAddress=InetAddress.getByName(msname.replaceAll("[^\\d.]", ""));
			System.out.println("Il socket è "+socket+" porta "+port+" msAddress "+msAddress);
			socket.joinGroup(msAddress);
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			 byte[] buf = new byte[BUFFER_LEN];
			 DatagramPacket recv = new DatagramPacket(buf, buf.length);
			 chatroomGUI.setConversationArea(recv.toString());
			try {
				System.out.println("In attesa di un pacchetto");
				socket.receive(recv);
				System.out.println("RICEVUTO");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
