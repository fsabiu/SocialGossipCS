package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import communication.RequestMessage;
import communication.ResponseMessage;
import util.Config;


/**
 * Client request manager class implementation
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class RequestManager implements Runnable {
	private Socket client_control;
	private Socket client_messages;
	private Graph<User> network;
	private ConcurrentHashMap<String, Chatroom> chatrooms;
	private ConcurrentHashMap<String, User> usersbyname;
	private PrivateMessageManager message_manager;
	private ReplyMessageManager response_manager;
	private NotificationManager notifier;	
	/**
	 * Constructor
	 * @param client
	 * @param network
	 * @param chatrooms
	 * @param usersbyname
	 * @throws IllegalArgumentException
	 */
	public RequestManager(Socket client_control,Graph<User> network, ConcurrentHashMap<String, Chatroom> chatrooms, ConcurrentHashMap<String, User> usersbyname) throws IllegalArgumentException {
		super();
		if(client_control==null || network==null || chatrooms==null || usersbyname==null) {
			throw new IllegalArgumentException();
		}
		this.client_control=client_control;
		this.client_messages=null;
		this.network=network;
		this.chatrooms=chatrooms;
		this.usersbyname=usersbyname;
		this.message_manager=null;
		this.response_manager= new ReplyMessageManager();
		this.notifier= new NotificationManager(usersbyname, chatrooms, network);
	}
	
	@Override
	public void run() {
		System.out.println("SERVER: Sono il request manager");
		
		
		//Declaring control stream addresses
		DataInputStream control_in = null;
		DataOutputStream control_out = null;	
		
		try {
			control_in = new DataInputStream(new BufferedInputStream(client_control.getInputStream()));
			control_out = new DataOutputStream(client_control.getOutputStream());
			
			//Reading handshake data
			client_control.setSoTimeout(1500);
			String handshake = control_in.readUTF();
			
			RequestMessage handshake_message=new RequestMessage();
			handshake_message.parseToMessage(handshake);
			
			//Getting IP from handshake message
			String IPString = (String) handshake_message.getParameter("IP");
			String portString = (String) handshake_message.getParameter("PORT");
			
			//Converting
			InetAddress IP= InetAddress.getByName(IPString);
			int port= Integer.parseInt(portString);
			
			System.out.println("Server: asking for connection to "+IPString+":"+portString);
			
			//Asking for connection
			client_messages=new Socket(IP, port);
			
			System.out.println("Server: hanshake with client terminated");
			
			User connection_user=null;
			
			//Repeat until the client is connected
			while(true) {
				try {
					//Reading client message
					String request = control_in.readUTF();

					//Parsing String to Message
					RequestMessage message=new RequestMessage();
					message.parseToMessage(request);
					
					//Debug print
					System.out.println("SERVER: received: "+request);
					
					//Executing client request
					connection_user= executeRequest(message,control_out);
				}
				//client closes connection
				catch(EOFException e) {
					System.out.println("Connection closed by client");
					//SE ERA UN UTENTE LOGGATO, SETTALO OFFLINE
					if (connection_user!=null) {
						message_manager.setSender(null);
						connection_user.setOffline();
					}
					break;
				}
				catch (IOException e1) {
					System.out.println("IO Exception while reading client message");
				}
			}
			
		} 
		catch (IOException e1) {
			System.out.println("IO Exception while instantiating client connection");
		}
		
		//Closing 
		finally {
			//{
				try {
					if(client_control != null) client_control.close();
					if(client_messages != null) client_messages.close();
					if(control_in != null) control_in.close();
					if(control_out != null) control_out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			//}
		}
	}


	private User executeRequest(RequestMessage message, DataOutputStream out) {
		
		User connection_user=null;
		
		//Creation reply message
		ResponseMessage reply=null;
		try {		
			//Getting operation from RequestMessage
			//Operation op = (Operation) message.getParameter("OPERATION");
			String op = (String) message.getParameter("OPERATION");
			String sender= (String) message.getParameter("SENDER");
			
			//Getting the user of current connection, if it exists
			//In this way, if he closes the connection we can set it offline 
			connection_user= usersbyname.get(sender);
			
			System.out.println("Server: l'operazione �: "+op);
			
			//Validating operation field
			if(op==null) {
				System.out.println("Invalid operation type recived");
				reply= new ResponseMessage();
				reply.setParameters("OPERATION:ERR","BODY:Invalid request received");
			}
			switch(op) {
				case "REGISTER":
					reply=registerUser(message);
					break;
				case "CHAT_ADDING":
					reply=addToChat(message);
					break;
				case "CHAT_CLOSING":
					reply=closeChat(message);
					break;
				case "CHAT_CREATION": 
					reply=createChat(message);
					break;
				case "CHAT_LISTING":
					reply=listChat(message);
					break;
				case "FILE_TO_FRIEND":
					reply=fileToFriend(message);
					break;
				case "FRIENDSHIP":
					reply=friendship(message);
					break;
				case "LIST_FRIENDS":
					reply=listFriends(message);
					break;
				case "LOGIN":
					reply=login(message, client_control, client_messages, out);
					break;
				case "LOGOUT":
					reply=logout(message);
					break;
				case "LOOKUP":
					reply=lookup(message);
					break;
				case "MSG_TO_CHATROOM":
					reply=msgToChatroom(message);
					break;
				case "MSG_TO_FRIEND":
					reply=msgToFriend(message);
					break;
				default:
					break;
			}
		} catch(IllegalArgumentException e) {
			System.exit(-1);
		}
		
		//Replying to sender
		if(message_manager!=null) {//replying to user 
			System.out.println("Sender: "+message_manager.getSender()+" � online? "+message_manager.getSender().isOnline());
			response_manager.sendMessageToUser(reply, message_manager.getSender());
		}else {//Sender was not a user
			response_manager.sendReply(reply, out);
		}
		return connection_user;
	}

	private ResponseMessage msgToChatroom(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String chat= (String) message.getParameter("CHATROOM");
		
		//To network node
		User sender_user=usersbyname.get(sender);
		Chatroom chatroom= chatrooms.get(chat);
		
		//if user does not belong to chatroom
		if(!chatroom.isParticipant(sender_user)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY: Not a member of "+chat+" chatroom");
			return reply;
		}
		
		//All fine. Getting dispatcher
		ChatroomManager dispatcher= chatroom.getDispatcher(chatroom);
		if(!dispatcher.sendMessage(message.toString())){
			reply.setParameters("OPERATION:ERR", "BODY: Error while dispatching message to chatroom");
			return reply;
		}
		
		//All fine
		reply.setParameters("OPERATION:OK");
		reply.setParameters("BODY:Messaggio inviato correttamente a "+chat+".");
		return reply;
	}

	private ResponseMessage listFriends(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		
		//To network node
		User sender_user=usersbyname.get(sender);
		
		//Getting friends
		Set<User> friends= network.getAdjVertices(sender_user);
		
		List<String> friendslist= new ArrayList<String>();
		
		for(User user: friends) {
			friendslist.add(user.getUsername());
		}
		
		reply.setParameters("OPERATION:OK", "BODY:"+friendslist);
		return reply;
	}

	private ResponseMessage friendship(RequestMessage message) {
		//Creation reply message 
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//To network nodes
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		//Is receiver a user?
		if(!usersbyname.containsKey(receiver)) {
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:Selected user does not exist");
			return reply;
		}
		
		//Are sender and receiver friends?	
		if(network.areAdj(sender_user, receiver_user)) {
			reply.setParameters("OPERATION:ERR", "BODY:You and "+receiver+" are already friends");
			return reply;
		}
		
		//Online checking
		boolean online;
		synchronized(receiver_user) {
			online=receiver_user.isOnline();
		}
		if(online) {
			network.addEdge(sender_user, receiver_user);
			reply.setParameters("OPERATION:OK");
			reply.setParameters("BODY:Tu e "+receiver+" siete ora amici!");
			notifier.notifyFriendship(sender_user, receiver_user);
		}else {
			reply.setParameters("OPERATION:USER_OFFLINE");
		}
		return reply;
	}

	private ResponseMessage fileToFriend(RequestMessage message) {
		//Creation reply message (it will contain both receiver IP and port)
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//To network nodes
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		//Is receiver a user?
		if(!usersbyname.containsKey(receiver)) {
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:Selected user does not exist");
			return reply;
		}
		
		//Are sender and receiver friends?	
		if(!network.areAdj(sender_user, receiver_user)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You and "+receiver+" are not friends");
			return reply;
		}
		
		//Is receiver online?
		boolean online;
		synchronized(receiver_user) {
			online=receiver_user.isOnline();
			if(!online) {
				reply.setParameters("OPERATION:ERR", "BODY:"+receiver+" is offline");
				return reply;
			}
		}
		
		//Getting receiver IP and port and writing them into reply
		reply=message_manager.getReceiverFileSocket(message, sender_user, receiver_user);
		if(reply==null) {//In case of error
			reply= new ResponseMessage();
			reply.setParameters("OPERATION:ERR", "BODY:Error while contacting with "+receiver);
		} else {//Else
			reply.setParameters("OPERATION:OK");
			reply.setParameters("BODY:File inviato con successo!");
		}
		return reply;
	}


	/**
	 * REQUIRES: 
	 * 	-	sender_user and receiver user are friends
	 * The function translates the message into the receiver language
	 * If receiver_user is online, the message is sent
	 * @param message
	 * @return
	 */
	private ResponseMessage msgToFriend(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//To network nodes
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		//If user doesn't exist
		if(!usersbyname.containsKey(sender)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;
		}
				
		//Is receiver a user?
		if(!usersbyname.containsKey(receiver)) {
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:Selected user does not exist");
			return reply;
		}
		
		//Are sender and receiver friends?	
		if(!network.areAdj(sender_user, receiver_user)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
			return reply;
		}
			
		//Is receiver online?
		boolean online;
		synchronized(receiver_user) {
			online=receiver_user.isOnline();
			if(!online) {
				reply.setParameters("OPERATION:ERR", "BODY:"+receiver+" � offline!");
				return reply;
			}
		}
		
		//Setting reply
		reply.setParameters("OPERATION:OK"); 
		reply.setParameters("BODY:Messaggio inviato con successo");
		
		//Sending message
		if(!message_manager.sendMessageToUser(message, receiver_user)) {
			reply.setParameters("OPERATION:ERR");
		}
		return reply;
	}

	private ResponseMessage lookup(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String sender= (String) message.getParameter("SENDER");
		String username= (String) message.getParameter("USERNAME");
		
		//If user doesn't exist
		if(!usersbyname.containsKey(sender)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;
		}
				
		//Setting reply
		reply.setParameters("OPERATION:OK");
		if(usersbyname.containsKey(username)) reply.setParameters("BODY:"+username+" � iscritto!");
		else reply.setParameters("BODY:"+"Ci dispiace, "+sender+". "+username+" non � ancora iscritto. Perch� non lo inviti?");
		return reply;
	}

	private ResponseMessage logout(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("SENDER");
		
		//If user doesn't exist
		if(!usersbyname.containsKey(username)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;
		}
		
		//Getting user
		User user=usersbyname.get(username);

		//Setting offline
		user.setOffline();
		
		reply.setParameters("OPERATION:OK");
		reply.setParameters("BODY:A presto, "+username+"!");
		return reply;
	}

	/**
	 * Assigns the current user to the PrivateMessageManager
	 * Checks for errors and sets the user online
	 * @param message
	 * @param client_control
	 * @param client_messages
	 * @return
	 */
	private ResponseMessage login(RequestMessage message, Socket client_control, Socket client_messages, DataOutputStream out) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String username= (String) message.getParameter("SENDER");
		String password= (String) message.getParameter("PASSWORD");
		
		System.out.println("Il sender � "+username+" la password "+password);
		
		//If user doesn't exist
		if(!usersbyname.containsKey(username)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;
		}
			
		
		if(!password.equals(usersbyname.get(username).getPassword())) {
			reply.setParameters("OPERATION:INVALID_CREDENTIALS");
			return reply;
		}

		//Getting user
		User user=usersbyname.get(username);
		
		//Setting user as online
		user.setOnline(client_control, client_messages, out);
		
		//Setting reply
		reply.setParameters("OPERATION:OK");
		reply.setParameters("BODY:Bentornato, "+username+". Inizia a chattare!");
		
		//Notify friends
		notifier.notifyOnlineFriend(user);
		
		System.out.println("Nome utente "+user.toString());
		
		//Adding user to PrivateMessageManager for future message requests
		message_manager=new PrivateMessageManager();
		message_manager.setSender(user);
		
		System.out.println("Terminato il login");
		return reply;
		}

	private ResponseMessage listChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("SENDER");
		
		//List of chatroom the user belongs to
		ArrayList<String> belonglist= new ArrayList<String>();
		
		//List of chatroom the user does not belong to
		ArrayList<String> notbelonglist= new ArrayList<String>();
		
		//Filling lists
		for(Chatroom chatroom: chatrooms.values()) {
			if(chatroom.isParticipant(usersbyname.get(username)))
				belonglist.add(chatroom.getName());
			else notbelonglist.add(chatroom.getName());
		}
		
		//Filling reply
		reply.setParameters("OPERATION:OK", "BELONGS:"+belonglist, "NOT BELONGS:"+notbelonglist);
		return reply;
	}

	private ResponseMessage createChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String chatroom= (String) message.getParameter("CHATROOM");
		
		//Is the sender a user?
		/*if(!usersbyname.containsKey(username)) 
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
		
		else */
		//Does chatroom already exist?
		if(chatrooms.containsKey(chatroom)) {
			reply.setParameters("OPERATION:CHATROOM_ALREADY_EXISTS");
			return reply;
		}
		
		//Getting new chatroom parameters
		String msName= createChatroomAddress();
		
		//Checking chatroom address
		if(msName==null) {
			reply.setParameters("OPERATION:ERR", "BODY:Cannot create chatroom");
			return reply;
		}
		
		//multicast address
		InetAddress msAddress = null;
		try {
			msAddress = InetAddress.getByName(msName);
		} catch (UnknownHostException e1) {
			reply.setParameters("OPERATION:ERR", "BODY:Network error while creating server socket");
			return reply;
		}
		InetAddress listenAddress = null;
		try {
			listenAddress = InetAddress.getByName(Config.SERVER_HOST_NAME);
		} catch (UnknownHostException e1) {
			reply.setParameters("OPERATION:ERR", "BODY:Network error while defining server address");
			return reply;
		}
		
		//Creating new chatroom
		try {
			Chatroom new_chatroom= new Chatroom(chatroom, usersbyname.get(username), msAddress, listenAddress);
			chatrooms.putIfAbsent(chatroom, new_chatroom);
			reply.setParameters("OPERATION:OK");
			reply.setParameters("BODY:Chatroom "+chatroom+" creata con successo!");
			return reply;
		} catch (Exception e) {
			reply.setParameters("OPERATION:ERR", "BODY:Network error while creating chatroom");
			return reply;
		}	
	}

	private synchronized String createChatroomAddress() {
		String[] IP= Config.FIRST_MULTICAST_ADDR.split("\\.");
		Integer offset= Integer.parseInt(IP[3]);
		offset=offset+chatrooms.size();
		
		if(offset.equals(256)) return null;
		
		return new String(IP[0]+"."+IP[1]+"."+IP[2]+"."+offset.toString());
	}

	private ResponseMessage closeChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String chat= (String) message.getParameter("CHATROOM");
		

		//Is the sender a user?
		/*if(!usersbyname.containsKey(username)) 
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
		else */
		
		//If the chatroom doesn't exists
		if(!chatrooms.containsKey(chat)) {
			reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
			return reply;
		}
		
		//Getting chatroom
		Chatroom chatroom=chatrooms.get(chat);
		
		//Is the user an administrator?
		if(!chatroom.deleteChatroom(usersbyname.get(username))) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not an administrator");
			return reply;
		} 
		
		//else
		reply.setParameters("OPERATION:OK");
		
		//Notify users
		notifier.notifyChatroomClosing(chatroom);
		
		//Deleting chat
		chatrooms.remove(chat);
		
		return reply;
	}

	private ResponseMessage addToChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String chat= (String) message.getParameter("CHATROOM");
		
		//Is the sender is not a user
		/*if(!usersbyname.containsKey(username)) 
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;*/
		
		//If chatroom doesn't exist
		if(!chatrooms.containsKey(chat)) { //Does chatroom exist?
			reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
			return reply;
		}

		//Adding user to chatroom
		Chatroom chatroom=chatrooms.get(chat);
		User user= usersbyname.get(username);
		chatroom.addParticipant(user);
		reply.setParameters("OPERATION:OK");
		reply.setParameters("IP:"+chatroom.getAddress());
		reply.setParameters("PORT:"+chatroom.getPort());
		
		//Notify users
		notifier.notifyChatroomJoin(user, chatroom);
		return reply;
	}

	private ResponseMessage registerUser(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String password= (String) message.getParameter("PASSWORD");
		String language= (String) message.getParameter("LANGUAGE");
		System.out.println("Server: sono in register");
		try {
			//If user already exists
			if(usersbyname.containsKey(username)) {
				reply.setParameters("OPERATION:USER_ALREADY_EXISTS","BODY:Username already exists");
				return reply;
			}
			
			//Adding user to data structures
			User new_user= new User(username, password, language);
			usersbyname.putIfAbsent(username, new_user);
			network.addVertex(new_user);
			
			//Setting up reply
			reply.setParameters("OPERATION:OK");
			reply.setParameters("BODY:Benvenuto in SocialGossip, "+username+": ora sei un utente!");
			return reply;
		}catch(IllegalArgumentException e) {
			reply.setParameters("OPERATION:ERR","BODY:Invalid parameters");
			return reply;
		}
	}

	void chatState(ConcurrentHashMap<String, Chatroom> chatrooms, ConcurrentHashMap<String, User> usersbyname, Graph<User> network) {
		
		//PRINT USERS
		System.out.println("UTENTI");
		for(String user: usersbyname.keySet()) {
			System.out.println(user);
		}
		
		//PRINT ONLINE USERS
		System.out.println("\nUTENTI ONLINE");
		for(String user: usersbyname.keySet()) {
			if(usersbyname.get(user).isOnline())
				System.out.println(user);
		}
		
		//PRINT FRIENDSHIPS
		System.out.println("\nAMICIZIE");
		for(String user: usersbyname.keySet()) {
			 Set<User> friends= network.getAdjVertices((usersbyname.get(user)));
			 for(User u: friends) {
				 System.out.println(user+" - "+u.getUsername());
			 }
		}
		
		//PRINT CHATROOMS
		System.out.println("\nCHATROOMS");
		for(String chatroom: chatrooms.keySet()) {
				 System.out.println(chatroom);
				 System.out.println("Partecipanti: ");
				 for (User u:chatrooms.get(chatroom).getParticipants()) {
					 System.out.println(u.getUsername());
				 }
		}
	}
}