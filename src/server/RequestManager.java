package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		ObjectInputStream control_in = null;
		DataInputStream control_data_in = null;
		DataOutputStream control_data_out = null;	
		ObjectOutputStream control_out = null;
		
		try {
			control_data_in = new DataInputStream(new BufferedInputStream(client_control.getInputStream()));
			control_data_out = new DataOutputStream(client_control.getOutputStream());
			control_in = new ObjectInputStream(control_data_in);
			control_out = new ObjectOutputStream(control_data_out);
			
			//Reading handshake data
			String handshake = control_data_in.readUTF();
			
			System.out.println("Handshake letto");
			
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
			
			DataInputStream message_data_in= new DataInputStream(new BufferedInputStream(client_messages.getInputStream()));
			DataOutputStream message_data_out= new DataOutputStream(client_messages.getOutputStream());
			ObjectOutputStream message_out= new ObjectOutputStream(message_data_out);
			ObjectInputStream message_in= new ObjectInputStream(message_data_in);
			
			System.out.println("Server: hanshake with client terminated");
			
			User connection_user=null;
			
			//Repeat while the client is connected
			while(true) {
				try {
					//Reading client message
					Object message=null;
					try {
						message = (Object) control_in.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Dopo handshake: "+message.toString());
					
					if(message instanceof RequestMessage) {
						//Executing client request if message is RequestMessage
						connection_user= executeRequest((RequestMessage)message,control_out,message_out, control_in, message_in);
					}
					
					if(message instanceof ResponseMessage) {
						//Processing client reply
						connection_user= processResponse((RequestMessage)message,control_out,message_out);
					}
				}
				//client closes connection
				catch(EOFException e) {
					//SE ERA UN UTENTE LOGGATO, SETTALO OFFLINE
					if (connection_user!=null) {
						message_manager.setSender(null);
						connection_user.setOffline();
						System.out.println(connection_user.getUsername()+" disconnected");
					} else {
						System.out.println("Connection closed by anonymous client");
					}
					break;
				}
				catch (IOException e1) {
					//SE ERA UN UTENTE LOGGATO, SETTALO OFFLINE
					if (connection_user!=null) {
						message_manager.setSender(null);
						connection_user.setOffline();
						System.out.println(connection_user.getUsername()+" disconnected");
					} else {
						System.out.println("Connection closed by anonymous client");
					}
					break;
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


	private User processResponse(RequestMessage message, ObjectOutputStream control_out, ObjectOutputStream message_out) {
		//Creation reply message
		ResponseMessage reply=null;
		
		//Getting operation from ResponseMessage
		String op = (String) message.getParameter("OPERATION");
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//To network node
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		//Switching operation
		switch(op) {
		case "FILE_TO_FRIEND":
			
			//Online checking
			boolean online;
			synchronized(receiver_user) {
				online=receiver_user.isOnline();
			}
			if(online) {
				response_manager.sendReply(reply, control_out);
			}
			break;
		}
		return sender_user;
	}

	@SuppressWarnings("null")
	private User executeRequest(RequestMessage message, ObjectOutputStream control_out, ObjectOutputStream message_out, ObjectInputStream control_in, ObjectInputStream message_in) {
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
			
			//Validating operation field
			if(op==null) {
				System.out.println("Invalid operation type recived");
				reply.setParameters("OPERATION:ERR","BODY:Invalid request received");
			}/*else if (op == "MSG_TO_FRIEND"){//Checking for self-messages
				String receiver= (String) message.getParameter("RECEIVER");
				if(receiver!=null) {
					if(sender.equals(receiver)){
						reply.setParameters("OPERATION:ERR","BODY:Non puoi essere tu il destinatario del messaggio!");
						op="";
					}
				}
			}*/
			
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
					reply=login(message, client_control, client_messages, control_out, message_out, control_in, message_in);
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
			reply.setParameters("RESPONSE_TYPE:"+op);
		} catch(IllegalArgumentException e) {
			System.exit(-1);
		}
		
		//Replying to sender
		if(message_manager!=null) {//replying to user 
			System.out.println(connection_user.getUsername());
			System.out.println("Message out: "+connection_user.getMessageOutputStream());
			response_manager.sendMessageToUser(reply, message_manager.getSender());
		}else {//Sender was not a user
			response_manager.sendReply(reply, control_out);
		}
		
		System.out.println("Risposta inviata");
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
		ChatroomManager dispatcher= chatroom.getDispatcher();
		if(!dispatcher.sendMessage(message)){
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
		
		//Print
		//String res= (String) reply.getParameter("BODY");
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
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:L'utente "+receiver+" non esiste!");
			return reply;
		}
		
		//Are sender and receiver friends?	
		if(network.areAdj(sender_user, receiver_user)) {
			reply.setParameters("OPERATION:ERR", "BODY:Tu e "+receiver+" siete già amici.");
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
			reply.setParameters("BODY:"+receiver+" non è online, riprova più tardi!");
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
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:L'utente "+receiver+" non esiste!");
			return reply;
		}
		
		//Are sender and receiver friends?	
		if(!network.areAdj(sender_user, receiver_user)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:Tu e "+receiver+" non siete amici. Aggiungilo"
					+ " ai tuoi amici per inviargli un file.");
			return reply;
		}
		
		//Is receiver online?
		boolean online;
		synchronized(receiver_user) {
			online=receiver_user.isOnline();
			if(!online) {
				reply.setParameters("OPERATION:USER_OFFLINE", "BODY:"+receiver+" is offline");
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
	private ResponseMessage msgToFriend(RequestMessage request) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) request.getParameter("SENDER");
		String receiver= (String) request.getParameter("RECEIVER");
		
		//To network nodes
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		//If user doesn't exist
		if(!usersbyname.containsKey(sender)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:L'utente "+sender+" non esiste!");
			return reply;
		}
				
		//Is receiver a user?
		if(!usersbyname.containsKey(receiver)) {
			reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:L'utente "+receiver+" non esiste!");
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
				reply.setParameters("OPERATION:ERR", "BODY:"+receiver+" è offline!");
				return reply;
			}
		}
		
		
		
		//leggi mess mittente (con timer)
		RequestMessage message = null;
		try {
			System.out.println("Provo a leggere il messaggio");
			message = (RequestMessage) sender_user.getMessageInputStream().readObject();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Setting reply
		reply.setParameters("OPERATION:OK"); 
		reply.setParameters("BODY:"+message.getParameter("BODY"));
		reply.setParameters("SENDER:"+sender);
		reply.setParameters("RECEIVER:"+receiver);
		
		System.out.println("Messaggio letto: "+message.toString());
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
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:L'utente "+sender+" non esiste!");
			return reply;
		}
				
		//Setting reply
		reply.setParameters("OPERATION:OK");
		if(usersbyname.containsKey(username)) reply.setParameters("BODY:"+username+" è iscritto!");
		else reply.setParameters("BODY:"+"Ci dispiace, "+sender+". "+username+" non è ancora iscritto. Perchè non lo inviti?");
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
		
		//Unbounding message manager
		message_manager=null;
		
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
	private ResponseMessage login(RequestMessage message, Socket client_control, Socket client_messages, ObjectOutputStream control_out, ObjectOutputStream message_out, ObjectInputStream control_in, ObjectInputStream message_in) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String username= (String) message.getParameter("SENDER");
		String password= (String) message.getParameter("PASSWORD");
		
		System.out.println("Il sender è "+username+" la password "+password);
		
		//If username is not valid
		if(username.length()==0) {
			reply.setParameters("OPERATION:ERR","BODY:Invalid username");
			return reply;
		}
		
		//If user doesn't exist
		if(!usersbyname.containsKey(username)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:"+username+" non è un utente di SocialGossip. "
					+ "Registrati per accedere");
			return reply;
		}
		
		if(!password.equals(usersbyname.get(username).getPassword())) {
			reply.setParameters("OPERATION:INVALID_CREDENTIALS");
			return reply;
		}

		//Getting user
		User user=usersbyname.get(username);
		
		//Setting user as online
		try {
			user.setOnline(client_control, client_messages, control_out, message_out, control_in, message_in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Setting reply
		reply.setParameters("OPERATION:OK");
		reply.setParameters("USER:"+username);
		reply.setParameters("BODY:Bentornato, "+username+". Inizia a chattare!");
		
		//Notify friends
		notifier.notifyOnlineFriend(user);
		
		//Adding user to PrivateMessageManager for future message requests
		message_manager=new PrivateMessageManager();
		message_manager.setSender(user);
		
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
		reply.setParameters("OPERATION:OK", "BELONGS:"+belonglist, "NOT_BELONGS:"+notbelonglist);
		return reply;
	}

	private ResponseMessage createChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String chatroom= (String) message.getParameter("CHATROOM");
		
		//Is the sender a user?
		if(!usersbyname.containsKey(username)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;
		}
		
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
			System.out.println("msAddress generato: "+msAddress);
		} catch (UnknownHostException e1) {
			reply.setParameters("OPERATION:ERR", "BODY:Network error while creating server socket");
			return reply;
		}
		
		//Creating new chatroom
		try {
			Chatroom new_chatroom= new Chatroom(chatroom, usersbyname.get(username), msName, msAddress);
			chatrooms.putIfAbsent(chatroom, new_chatroom);
			int port = new_chatroom.getPort(); 
			reply.setParameters("OPERATION:OK");
			reply.setParameters("BODY:Chatroom "+chatroom+" creata con successo!");
			reply.setParameters("CHATROOM:"+chatroom);
			reply.setParameters("PORT:"+port);
			reply.setParameters("MSNAME:"+msName);
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
		
		//If the chatroom doesn't exist
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
		reply.setParameters("BODY:"+chat+" chiusa con successo.");
		
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
		if(!usersbyname.containsKey(username)) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:L'utente "+username+" non esiste!");
			return reply;
		}
		
		//If chatroom doesn't exist
		if(!chatrooms.containsKey(chat)) { //Does chatroom exist?
			reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
			return reply;
		}
		
		//If user already belongs to chatroom
		if(chatrooms.get(chat).isParticipant(usersbyname.get(username))) {
			reply.setParameters("OPERATION:ERR","BODY:L'utente "+username+" fa già parte della chatroom!");
			return reply;
		}

		//Adding user to chatroom
		Chatroom chatroom=chatrooms.get(chat);
		User user= usersbyname.get(username);
		chatroom.addParticipant(user);
		reply.setParameters("OPERATION:OK");
		reply.setParameters("BODY:Benvenuto in "+chat+"!");
		reply.setParameters("CHATROOM:"+chat);
		reply.setParameters("PORT:"+chatroom.getPort());
		reply.setParameters("MSNAME:"+chatroom.getMsName());
		
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
		try {
			//If user already exists
			if(usersbyname.containsKey(username)) {
				reply.setParameters("OPERATION:USER_ALREADY_EXISTS","BODY:Il nome utente "+username+" esiste già!");
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