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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIServerInterface;
import communication.RequestMessage;
import communication.RMIClientInterface;
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
			
			
			System.out.println("Connection established with server");
			
			try {
				DataInputStream control_in= new DataInputStream(new BufferedInputStream(server_control_socket.getInputStream()));
				DataOutputStream control_data_out= new DataOutputStream(server_control_socket.getOutputStream());
				ObjectOutputStream control_out = new ObjectOutputStream(control_data_out);
				
				ConcurrentHashMap<String,GUI> interfaces = new ConcurrentHashMap<String,GUI>();
				
				//Setting new message connection with Server
				setMessageConnection(control_data_out,interfaces);
				
				DataOutputStream message_data_out = new DataOutputStream(server_message_socket.getOutputStream());
				ObjectOutputStream message_out = new ObjectOutputStream(message_data_out);
				
				MessageSender message_sender= new MessageSender(control_out,message_out,server_message_socket,interfaces);
				
				System.out.println("Creato messge sender");
				
				loginGUI=new LoginGUI(message_sender);
				interfaces.putIfAbsent("loginGUI", loginGUI);
				loginGUI.setVisible(true); 

				//init RMI
				RMIServerInterface serverRMI = null;
				RMIClientInterface callback = null;
				startRMI(serverRMI, callback);
				
				MessageListener message_listener = new MessageListener(control_in, message_sender,interfaces,serverRMI,callback);
				message_listener.start();
				
				
				
			} catch (IOException e) {
				System.out.println("Error creating Streams IN/OUT");
				e.printStackTrace();
			}

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
		} /*finally {
			try {
				server_control_socket.close();
				server_message_socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
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
			
			PrivateMessageListener privateMessageListener = new PrivateMessageListener(interfaces,message_in); 
			privateMessageListener.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void startRMI(RMIServerInterface serverRMI, RMIClientInterface callback) {
		//cerco registro
		try {
			Registry registry = LocateRegistry.getRegistry(Config.SERVER_RMI_PORT);
			serverRMI = (RMIServerInterface) registry.lookup(Config.SERVER_RMI_SERVICE_NAME);

			//creo la classe che implementa le callback
			callback = new NotificationReceiver();
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//esporto la callback sul registro
		//NotificationReceiver stub = (NotificationReceiver)UnicastRemoteObject.exportObject(callback,0);
	}

	public Client(String ip) {
		this.message_ip=ip;
	}

	public static void main(String args[]) {
		
		//Client client=new Client(args[0]);
		Client client=new Client("10.0.0.17");
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
