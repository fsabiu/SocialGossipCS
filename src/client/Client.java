package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import communication.RequestMessage;
import util.Config;
import util.PortScanner;

public class Client implements Runnable{
	private String message_ip;
	private Socket server_control_socket;
	private Socket server_message_socket;
	private static LoginGUI loginGUI;
	private static String hostname;
	private ConcurrentHashMap<String,GUI> interfaces;
	private PrivateMessageListener privateMessageListener;
	
	public Client() {
	}
	
	@Override
	public void run() {
		try {
			//Starting new control connection with Server
			Socket server_control= new Socket(InetAddress.getByName(Config.SERVER_HOST_NAME),Config.SERVER_TCP_PORT);
			setServerControlSocket(server_control);
			
			
			System.out.println("Connection established with server");
			
			try {
				DataInputStream control_in= new DataInputStream(new BufferedInputStream(server_control_socket.getInputStream()));
				DataOutputStream control_data_out= new DataOutputStream(server_control_socket.getOutputStream());
				ObjectOutputStream control_out = new ObjectOutputStream(control_data_out);
				
				interfaces = new ConcurrentHashMap<String,GUI>();
				
				//Setting new message connection with Server
				setMessageConnection(control_data_out,interfaces);
				
				DataOutputStream message_data_out = new DataOutputStream(server_message_socket.getOutputStream());
				ObjectOutputStream message_out = new ObjectOutputStream(message_data_out);

				//Init RMI
				/*RMIClientInterface callback = null;
				callback = startRMI(serverRMI);
				*/
				MessageSender message_sender= new MessageSender(control_out,message_out,server_message_socket,interfaces);
				
				loginGUI=new LoginGUI(message_sender);
				interfaces.putIfAbsent("loginGUI", loginGUI);
				loginGUI.setVisible(true);
				
				MessageListener message_listener = new MessageListener(control_in,message_sender,interfaces,hostname);
				message_listener.start();
				
				//privateMessageListener.join();
				message_listener.join();
			} catch (IOException e) {
				System.out.println("Error creating Streams IN/OUT");
			}  catch (InterruptedException e) {
				System.out.println("Thread client stops");
			}
			finally {
				try {
					server_control_socket.close();
					server_message_socket.close();
					System.out.println("Client closed");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Connection closed by server");
		}
	}
	
	@SuppressWarnings("resource")
	private void setMessageConnection(DataOutputStream control_data_out, ConcurrentHashMap<String,GUI> interfaces) {
		// TODO Auto-generated method stub
		//Creating in/out streams
		try {
			//Setting IP and port in a JSONMessage
			RequestMessage handshake= new RequestMessage();
			int message_port=PortScanner.freePort();
			handshake.setParameters("IP:"+message_ip,"PORT:"+message_port);
			String handshake_string=handshake.toString();
			
			//Sending client IP and client port to receive private messages
			control_data_out.writeUTF(handshake_string);
			
			//Accepting new stream I/O to receive messages
			ServerSocket message_socket= new ServerSocket(message_port);
			Socket server_message_socket=message_socket.accept();
			setServerMessageSocket(server_message_socket);
			
			DataInputStream message_data_in = new DataInputStream(new BufferedInputStream(server_message_socket.getInputStream()));
			ObjectInputStream message_in = new ObjectInputStream(message_data_in);
			
			privateMessageListener = new PrivateMessageListener(interfaces,message_in); 
			privateMessageListener.start();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Client(String ip) {
		this.message_ip=ip;
	}

	public static void main(String args[]) {
		if(args.length!=2) {
			System.out.println("Launch with \"client.java YOUR_IP_ADDRESS SERVER_IP_ADDRESS\"");
			System.exit(-1);
		} 
		
		hostname = args[0];
		Config.SERVER_HOST_NAME = args[1];
		
		try {
			InetAddress.getByName(hostname).getHostAddress().equals(hostname);
			InetAddress.getByName(Config.SERVER_HOST_NAME).getHostAddress().equals(Config.SERVER_HOST_NAME);
		} catch (UnknownHostException e) {
			System.out.println("Invalid IP address received.");
			System.out.println("Launch with \"client.java YOUR_IP_ADDRESS SERVER_IP_ADDRESS\"");
			System.exit(-2);
		}

		Client client=new Client(hostname);
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
