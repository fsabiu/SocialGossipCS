package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import communication.RequestMessage;

public class ChatroomManager {
	
	//Listener configuration
	private MulticastSocket socket; //Chatroom multicast socket
	private InetAddress msAddress; //Chatroom address
	
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
	
	public boolean sendMessage(RequestMessage message) {
		try {
			byte[] msg= objectToByte(message);
			forwardMessage(msg);
			System.out.println("###########\n"+message+"\nINVIATO");
			return true;
		}catch(IOException e) {
			return false;
		}
	}
	
	private byte[] objectToByte(RequestMessage message) {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos;
	    try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		return bos.toByteArray();
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
