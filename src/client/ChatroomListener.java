package client;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import communication.RequestMessage;

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
			this.msAddress=InetAddress.getByName(msname);
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
			try {
				socket.receive(recv);
				RequestMessage ric = (RequestMessage) BytestoObject(recv.getData());
				chatroomGUI.setConversationArea("["+ric.getParameter("SENDER")+": ] "+ric.getParameter("BODY").toString());
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Object BytestoObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        } finally {
            if (bis != null) {
                bis.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
        return obj;
    }
}
