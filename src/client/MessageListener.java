package client;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import communication.RMIClientInterface;
import communication.RMIServerInterface;
import communication.RequestMessage;
import communication.ResponseMessage;
import util.Config;
import util.PortScanner;

public class MessageListener extends Thread{

	private MessageSender message_sender;
	private DataInputStream control_in;
	private ConcurrentHashMap<String,GUI> interfaces;
	
	//It is used to receive Objects over socket
	private ObjectInputStream object_control_in = null;
	private String hostname;
	private RMIServerInterface serverRMI = null;
	private RMIClientInterface callback = null;
	
	//To stop thread running
	//private volatile boolean running = true;
	
	public MessageListener(DataInputStream control_in, MessageSender message_sender, ConcurrentHashMap<String,GUI> interfaces, String hostname) throws IOException {
		this.interfaces = interfaces;
		this.message_sender = message_sender;
		this.control_in = control_in;
		this.hostname = hostname;
		object_control_in = new ObjectInputStream(control_in);
	}

	public void run() {
		Object received_message;
		while(true) {
			//Receiving request
			received_message=receiveResponse();
			if (received_message instanceof RequestMessage) {
				RequestMessage request_message = (RequestMessage) received_message;
				checkRequest(request_message);
			} else if(received_message instanceof ResponseMessage) {
				ResponseMessage response = (ResponseMessage) received_message;
				checkResponse(response);
			}
		}
	}
	
	/*
	 * TODO Il costruttore di Message listener prende il control_in, mentre il costruttore di message
	 * sender deve prendere solo il control_out
	 * 
	 * Fare lo switch su Request_Type (?)
	 * 
	 */
	
	private void checkRequest(RequestMessage request) {
		String op = (String) request.getParameter("OPERATION");
		switch (op) {
			case "FILE_TO_FRIEND": {
				String sender = (String) request.getParameter("SENDER");
				String receiver = (String) request.getParameter("RECEIVER");
				String filename = (String) request.getParameter("FILENAME");
				//Setting up network settings to receive a new file
				ResponseMessage res = new ResponseMessage();
				res.setParameters("OPERATION:OK","TYPE:response","HOSTNAME:"+hostname,"FILENAME:"+filename, "RESPONSE_TYPE:"+op);
				res.setParameters("SENDER:"+receiver);
				res.setParameters("RECEIVER:"+sender);
				int port = PortScanner.freePort();
				System.out.println(port);
				res.setParameters("PORT:"+port);
				new FileReceiver(hostname,port,interfaces.get("chatGUI"+sender),sender,filename).start();
				message_sender.sendResponse(res);
			}
			break;
			default: {
				System.out.println("Ricevuta richiesta non valida");
			}
			break;
		}
		
		
	}

	public void checkResponse(ResponseMessage reply) {
		String op = (String) reply.getParameter("RESPONSE_TYPE");
		switch(op) {
			case "REGISTER":{
				((RegistrationGUI) interfaces.get("registrationGUI")).getRegistrationReply().setText((String) reply.getParameter("BODY"));
			}
			break;
			case "LOGIN":{
				GUI loginGUI = interfaces.get("loginGUI");
				//Showing the result to user
				((LoginGUI) loginGUI).getLoginResponse().setText((String) reply.getParameter("BODY"));
				
				if (reply.getParameter("OPERATION").equals("OK")) {
					
					loginGUI.setVisible(false);
					
					String user = (String) reply.getParameter("USER");
					
					// Requesting friend list
					RequestMessage req_friends = new RequestMessage();
					req_friends.setParameters("SENDER:"+user);
					req_friends.setParameters("OPERATION:LIST_FRIENDS");
					message_sender.sendRequest(req_friends);

					// Requesting chatroom list
					RequestMessage req_chatroom = new RequestMessage();
					req_chatroom.setParameters("SENDER:"+user);
					req_chatroom.setParameters("OPERATION:CHAT_LISTING");
					message_sender.sendRequest(req_chatroom);
					
					SocialGossipHomeGUI sgGUI = new SocialGossipHomeGUI( ((LoginGUI) interfaces.get("loginGUI")).getFrame(),user);
					sgGUI.setVisible(true);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);
					try {
						callback = startRMI(serverRMI);
						serverRMI.registerUserRMIChannel(user, callback);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((LoginGUI) loginGUI).getLoginResponse().setText("");
				}
			}
			break;
			case "LOGOUT":{
					
				//Getting response from server
				if (reply.getParameter("OPERATION").equals("OK")) {
					
					((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).logoutGUI();
					String user = (String) reply.getParameter("SENDER");
					interfaces.clear();
					LoginGUI loginGUI = new LoginGUI(message_sender);
					interfaces.putIfAbsent("loginGUI", loginGUI);
					loginGUI.setVisible(true);
					System.out.println("dOPO LA NEW");
					for (String elem : interfaces.keySet()) {
						System.out.println(elem);
					}
					try {
						serverRMI.unregisterUserRMIChannel(user, callback);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;
			case "LOOKUP":{
				System.out.println("Inviata richiesta di ricerca dell'utente");
				
				//Setting username and operation field
				/*req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_search= ((SocialGossipHomeGUI) gui).getUserSearchField().getText();
				req.setParameters("USERNAME:"+user_to_search);
				
				//Sending request to server
				sendRequest(req);*/
				
				//Getting response from server
				//ResponseMessage response=checkResponse();
				
				if (reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
				}
			}
			break;
			case "FRIENDSHIP":{
				System.out.println("Inviata richiesta di amicizia all'utente"+reply);				
				JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));

				// Preparing chat interface with the new friend
				String friend = (String) reply.getParameter("RECEIVER");
				ChatGUI chatGUI = new ChatGUI(friend);
				interfaces.putIfAbsent("chatGUI"+friend, chatGUI);
			} 
			break;
			case "LIST_FRIENDS":{
				if (reply.getParameter("OPERATION").equals("OK")) {
					//Server returns an ArrayList of strings
					
					//((SocialGossipHomeGUI) gui).setListFriends((String) second_response.getParameter("BODY"));
					//String list_friends = (String) reply.getParameter("BODY");
					
					if (reply.getParameter("BODY") != null) {
						String lista = (String) reply.getParameter("BODY");
						((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setListFriends(lista);
					}
				}
			}
			break;
			case "STARTCHAT": {
				System.out.println("Apertura chat");
				//Getting friend name
				
				//((SocialGossipHomeGUI) gui).getSelectedListFriend();
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).getSelectedListFriend();
				
				//Creating chat to chat with the above friend
				//((SocialGossipHomeGUI) gui).createChatGUI();
				//((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).createChatGUI();
				
			}
			break;
			case "MSG_TO_FRIEND": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					String sender = (String) reply.getParameter("SENDER");
					String receiver = (String) reply.getParameter("RECEIVER");
					/*for(String s : interfaces.keySet()) {
						System.out.println(s);
					}*/
					
					ChatGUI chatGUI = (ChatGUI) interfaces.get("chatGUI"+receiver);
					chatGUI.setConversationArea("["+sender+"] "+reply.getParameter("BODY"));
				}
				else {
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
				}
			}
			break;
			case "CHAT_LISTING": {
				System.out.println((String) reply.getParameter("BELONGS")+(String) reply.getParameter("NOT_BELONGS"));
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setChatroomList((String) reply.getParameter("BELONGS"), (String) reply.getParameter("NOT_BELONGS"));
			} 
			break;
			case "CHAT_CREATION": {
				//Ricevo la risposta 
				if (reply.getParameter("OPERATION").equals("OK")) {
					//Stampo il messaggio di creazione corretta
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
					
					create_chatroom(reply);
				}
			}
			break;
			case "CHAT_ADDING": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));

					create_chatroom(reply);
					//((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).getSelectedListChatroom();
				}
			}
			break;
			case "FILE_TO_FRIEND": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					String hostname = (String) reply.getParameter("HOSTNAME");
					int port = Integer.parseInt((String) reply.getParameter("PORT"));
					String filename = (String) reply.getParameter("FILENAME");
					if(!sendFile(hostname, port, filename)) {
						JOptionPane.showMessageDialog(null, "Error while sending file");
					}
				}
			}
			break;
			default: {
				JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
			}
			break;
		}
	}

	private boolean sendFile(String hostname, int port, String filename) {
		SocketChannel receiver_peer = null; 
		try {
			receiver_peer = SocketChannel.open();
			receiver_peer.connect(new InetSocketAddress(hostname,(int) port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File("");
		file = new File(file.getAbsolutePath()+Config.UPLOAD_DIRECTORY+filename);
		
		if (!file.exists() || file.isDirectory()) {
			JOptionPane.showMessageDialog(null, "File non trovato oppure non valido");
			return false;
		}
		
		RandomAccessFile afile = null;
		FileChannel inchannel = null;
		try {
			afile = new RandomAccessFile(file,"r");
			
			//To read file
			inchannel = afile.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			
			//Reads bytes and write
			while(inchannel.read(buffer) > 0) {
				buffer.flip();
				
				//invio dati al client
				while(buffer.hasRemaining()) {
					receiver_peer.write(buffer);
				}
				
				buffer.clear();
			}
			
			afile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (receiver_peer != null) {
				try {
					receiver_peer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
		
		return true;
	}
	

	private RMIClientInterface startRMI(RMIServerInterface serverRMI) {
		NotificationReceiver callback = null;
		//cerco registro
		try {
			Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST_NAME, Config.SERVER_RMI_PORT);
			serverRMI = (RMIServerInterface) registry.lookup(Config.SERVER_RMI_SERVICE_NAME);
			this.serverRMI = serverRMI;
			//creo la classe che implementa le callback
			callback = new NotificationReceiver(interfaces);
		} catch (RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return callback;
		//esporto la callback sul registro
		//NotificationReceiver stub = (NotificationReceiver)UnicastRemoteObject.exportObject(callback,0);
	}

	private void create_chatroom(ResponseMessage reply) {
		//Interface creation
		String nome_chatroom=reply.getParameter("CHATROOM").toString();
		ChatroomGUI chatroomGUI = new ChatroomGUI(nome_chatroom);
		chatroomGUI.setVisible(true);
		interfaces.putIfAbsent("chatroomGUI"+nome_chatroom, chatroomGUI);
		
		String msname = (String) reply.getParameter("MSNAME");
		String port = (String) reply.getParameter("PORT");
		
		//Chatroom listener creation
		ChatroomListener chatroomListener = new ChatroomListener(chatroomGUI, msname, port);
		chatroomListener.start();
	}

	public Object receiveResponse() {
        Object comeON = null;
		try {
			comeON = object_control_in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Connection closed by server");
			System.exit(-2);
		} 
		return comeON;
	}
}
