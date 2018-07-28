package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import communication.RequestMessage;
import util.Config;
import util.PortScanner;

public class Client implements Runnable{
	private String message_ip;
	private Socket server_control_socket;
	private Socket server_message_socket;
	private static LoginGUI loginGUI;
	
	public Client() {
	}
	
	@Override
	public void run() {
		try {
			//Starting new control connection with Server
			Socket server_control= new Socket(InetAddress.getByName(Config.SERVER_HOST_NAME),Config.SERVER_TCP_PORT);
			setServerControlSocket(server_control);
			//Setting new message connection with Server
			setMessageConnection();
			
			System.out.println("Connection established with server");
			MessageSender message_sender= new MessageSender(server_control_socket,server_message_socket,loginGUI);
			MessageListener message_listener = new MessageListener(message_sender);
			message_listener.start();
			loginGUI=new LoginGUI(message_sender);
			loginGUI.setVisible(true);
			//Creating class used to send new requests 
			
			
			//Creating a thread used to listen private messages 
			//MessageListener message_listener= new MessageListener();
			//message_listener.start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {/*
			try {
				server_control_socket.close();
				server_message_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
	
	private void setMessageConnection() {
		// TODO Auto-generated method stub
		//Creating in/out streams
		DataOutputStream control_out;
		try {
			control_out = new DataOutputStream(server_control_socket.getOutputStream());

		//Setting IP and port in a JSONMessage
		RequestMessage handshake= new RequestMessage("");
		int message_port=PortScanner.freePort();
		handshake.setParameters("IP:"+message_ip,"PORT:"+message_port);
		String handshake_string=handshake.toString();
		
		//Sending client IP and client port to receive private messages
		control_out.writeUTF(handshake_string);
		
		//Accepting new stream I/O to receive messages
		ServerSocket message_socket= new ServerSocket(message_port);
		Socket server_message_socket=message_socket.accept();
		setServerMessageSocket(server_message_socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Client(String ip) {
		this.message_ip=ip;
	}

	public static void main(String args[]) {
		
		Client client=new Client(args[0]);
		//Client client=new Client("localhost");
		System.out.println("Client started");
		client.run();
	}

	public Socket getServerControlSocket() {
		return server_control_socket;
	}

	public void setServerControlSocket(Socket server_control_socket) {
		this.server_control_socket = server_control_socket;
	}

	public Socket getServerMessageSocket() {
		return server_message_socket;
	}

	public void setServerMessageSocket(Socket server_message_socket) {
		this.server_message_socket = server_message_socket;
	}
}
