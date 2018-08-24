package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import util.PortScanner;

public class ChatroomManager {
	
	//Listener configuration
	private MulticastSocket socket; //Chatroom multicast socket
	private InetAddress msAddress; //Chatroom address

	//Message buffer
	private byte[] buffer;
	private static final int BUFFER_LEN = 1024;
	
	public ChatroomManager(MulticastSocket socket, InetAddress msAddress) throws Exception {
		super();
		
		//Validating parameters
		if(socket == null || msAddress == null)
			throw new NullPointerException();
		
		//Instantiating properties
		this.socket=socket;
		this.msAddress=msAddress;
	}
	
//	public void run() {
//		buffer = new byte[BUFFER_LEN];
//		DatagramPacket receivedPacket = new DatagramPacket(buffer,buffer.length);
//
//		while(!Thread.interrupted())
//		{
//			try {				
//				//Receiving and storing datagram
//				serverSock.receive(receivedPacket);
//				byte[] message = new byte[receivedPacket.getLength()];
//				
//				//Copying and sending
//				System.arraycopy(receivedPacket.getData(),receivedPacket.getOffset(),message,0,message.length);
//				forwardMessage(message);				
//			} 
//			//timeout
//			catch(SocketTimeoutException e) {}
//			catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		//Closing socket
//		serverSock.close();
//	}
	
	private void forwardMessage(byte[] msg) throws IOException {
		if(msg == null)
			throw new NullPointerException();
		//DatagramPacket creation
		DatagramPacket message = new DatagramPacket(msg,msg.length,msAddress,socket.getLocalPort());
		System.out.println("Ms address: "+msAddress);
		System.out.println("Local port: "+socket.getLocalPort());
		System.out.println("Socket: "+socket);
		//Sending message
		socket.send(message);
	}
	
	public boolean sendMessage(String message) {
		try {
			byte[] msg= message.getBytes();
			forwardMessage(msg);
			System.out.println("###########\n"+message+"\nINVIATO");
			return true;
		}catch(IOException e) {
			return false;
		}
	}
	
	public int getSocketPort() {
		return socket.getLocalPort();
	}
	
	/*
	public InetAddress getAddress() {
		return msAddress;
	}
	*/

}
