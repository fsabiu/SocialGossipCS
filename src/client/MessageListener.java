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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import communication.RMIClientInterface;
import communication.RMIServerInterface;
import communication.RequestMessage;
import communication.ResponseMessage;
import util.Config;
import util.PortScanner;

/**
 * Class used to listen for private messages
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class MessageListener extends Thread{

	private MessageSender message_sender;
	private DataInputStream control_in;
	private ConcurrentHashMap<String,GUI> interfaces;
	
	//Structure used to receive Objects over socket
	private ObjectInputStream object_control_in = null;
	
	private String hostname;
	private RMIServerInterface serverRMI = null;
	private RMIClientInterface callback = null;
	private ChatroomListener chatroomListener;
	
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
			//Waiting for a request
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
				System.out.println("Received nov valid request");
			}
			break;
		}
	}

	public void checkResponse(ResponseMessage reply) {
		// Getting response type from server
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
					req_chatroom.setParameters("SENDER:"+user,"OPERATION:CHAT_LISTING");
					message_sender.sendRequest(req_chatroom);
					
					SocialGossipHomeGUI sgGUI = new SocialGossipHomeGUI( ((LoginGUI) interfaces.get("loginGUI")).getFrame(),user);
					sgGUI.setVisible(true);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);
					try {
						callback = startRMI(serverRMI);
						serverRMI.registerUserRMIChannel(user, callback);
					} catch (RemoteException e) {
						System.out.println("User RMI registration failed");
					}
					((LoginGUI) loginGUI).getLoginResponse().setText("");
				}
			}
			break;
			case "LOGOUT":{
				if (reply.getParameter("OPERATION").equals("OK")) {
					((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).logoutGUI();
					String user = (String) reply.getParameter("SENDER");
					interfaces.clear();
					LoginGUI loginGUI = new LoginGUI(message_sender);
					interfaces.putIfAbsent("loginGUI", loginGUI);
					loginGUI.setVisible(true);
					try {
						serverRMI.unregisterUserRMIChannel(user, callback);
					} catch (RemoteException e) {
						System.out.println("User RMI unregistration failed");
					}
				}
			}
			break;
			case "LOOKUP":{
				if (reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
					JDialog dialog = pane.createDialog(null, "Notification");
					dialog.setModal(false);
					dialog.setVisible(true);
				}
			}
			break;
			case "FRIENDSHIP":{
				System.out.println("Inviata richiesta di amicizia all'utente"+reply);				
				JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
				JDialog dialog = pane.createDialog(null, "Notification");
				dialog.setModal(false);
				dialog.setVisible(true);

				// Preparing chat interface with the new friend
				String friend = (String) reply.getParameter("RECEIVER");
				ChatGUI chatGUI = new ChatGUI(friend);
				interfaces.putIfAbsent("chatGUI"+friend, chatGUI);
			} 
			break;
			case "LIST_FRIENDS":{
				if (reply.getParameter("OPERATION").equals("OK")) {
					if (reply.getParameter("BODY") != null) {
						String lista = (String) reply.getParameter("BODY");
						((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setListFriends(lista);
					}
				}
			}
			break;
			case "STARTCHAT": {
				System.out.println("Apertura chat");
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).getSelectedListFriend();
			}
			break;
			case "STARTCHATROOM": {
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).getSelectedListChatroom();
			}
			break;
			case "MSG_TO_FRIEND": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					String sender = (String) reply.getParameter("SENDER");
					String receiver = (String) reply.getParameter("RECEIVER");

					//Showing message
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
				String not_belongs = (String) reply.getParameter("NOT_BELONGS");
				String belongs = (String) reply.getParameter("BELONGS");
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setChatroomList(belongs, not_belongs);
				belongs = belongs.replace("[", "").replace("]", "");
				String[] chatroomList = belongs.split(", ");
				
				if (belongs.isEmpty()) break;
				for (String chatroom : chatroomList) {
					String msname = (String) reply.getParameter(chatroom+"?MSNAME");
					String port = (String) reply.getParameter(chatroom+"?PORT");
					
					ChatroomGUI chatroomGUI;
					if(!interfaces.containsKey("chatroomGUI"+chatroom)) {
						chatroomGUI = new ChatroomGUI(chatroom);
						chatroomGUI.setVisible(false);
						interfaces.putIfAbsent("chatroomGUI"+chatroom, chatroomGUI);
					} else {
						chatroomGUI = (ChatroomGUI) interfaces.get("chatroomGUI"+chatroom);
					}
					
					//Chatroom listener creation
					ChatroomListener chatroomListener = new ChatroomListener(chatroomGUI, msname, port);
					chatroomListener.start();
				}
			} 
			break;
			case "CHAT_CREATION": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					create_chatroom(reply);
				}
				else {
					//Print error
					JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
		            JDialog dialog = pane.createDialog(null, "Error");
		            dialog.setModal(false);
		            dialog.setVisible(true);
				}
			}
			break;
			case "CHAT_ADDING": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
					create_chatroom(reply);
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
					else {
						JOptionPane pane = new JOptionPane(filename+" inviato correttamente.");
			            JDialog dialog = pane.createDialog(null, "Message");
			            dialog.setModal(false);
			            dialog.setVisible(true);
					}
				}
			}
			break;
			case "MSG_TO_CHATROOM": {
				if (!reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
		            JDialog dialog = pane.createDialog(null, "Error");
		            dialog.setModal(false);
		            dialog.setVisible(true);
				}
			}
			break;
			case "CHAT_CLOSING": {
				JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
	            JDialog dialog = pane.createDialog(null, "Error");
	            dialog.setModal(false);
	            dialog.setVisible(true);
	            if (reply.getParameter("OPERATION").equals("OK")) {
	            	chatroomListener.stopListening();
	            }
			}
			break;
			default: {
				JOptionPane pane = new JOptionPane(reply.getParameter("BODY"));
	            JDialog dialog = pane.createDialog(null, "Error");
	            dialog.setModal(false);
	            dialog.setVisible(true);
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
			System.out.println("Error while opening send file socket");
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

				while(buffer.hasRemaining()) {
					receiver_peer.write(buffer);
				}
				buffer.clear();
			}
			
			afile.close();
		} catch (IOException e) {
			System.out.println("Error creating file");
		} finally {
			if (receiver_peer != null) {
				try {
					receiver_peer.close();
				} catch (IOException e) {
					System.out.println("Error creating file");
				}
			}	
		}
		
		return true;
	}
	

	private RMIClientInterface startRMI(RMIServerInterface serverRMI) {
		NotificationReceiver callback = null;
		// Looking for a registry 
		try {
			Registry registry = LocateRegistry.getRegistry(Config.SERVER_HOST_NAME, Config.SERVER_RMI_PORT);
			serverRMI = (RMIServerInterface) registry.lookup(Config.SERVER_RMI_SERVICE_NAME);
			this.serverRMI = serverRMI;
			// Creating callback class implemantation
			callback = new NotificationReceiver(interfaces);
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Error starting RMI");
		}
		
		return callback;
	}

	private void create_chatroom(ResponseMessage reply) {
		//Interface creation
		String nome_chatroom=reply.getParameter("CHATROOM").toString();
		ChatroomGUI chatroomGUI;
		if(!interfaces.containsKey("chatroomGUI"+nome_chatroom)) {
			chatroomGUI = new ChatroomGUI(nome_chatroom);
			chatroomGUI.setVisible(true);
			interfaces.putIfAbsent("chatroomGUI"+nome_chatroom, chatroomGUI);
		} else {
			chatroomGUI = (ChatroomGUI) interfaces.get("chatroomGUI"+nome_chatroom);
		}
		
		String msname = (String) reply.getParameter("MSNAME");
		String port = (String) reply.getParameter("PORT");
		
		//Chatroom listener creation
		chatroomListener = new ChatroomListener(chatroomGUI, msname, port);
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
