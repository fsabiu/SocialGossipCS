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
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import communication.NotificationMessage;
import communication.Operation;
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
	}
	
	@Override
	public void run() {
		
		//Declaring control stream addresses
		DataInputStream control_in = null;
		DataOutputStream control_out = null;
		DataInputStream messages_in = null;
		DataOutputStream messages_out = null;		
		
		try {
			control_in = new DataInputStream(new BufferedInputStream(client_control.getInputStream()));
			control_out = new DataOutputStream(client_control.getOutputStream());
			
			//Reading handshake data
			String handshake = control_in.readUTF();
			JSONParser parser= new JSONParser();
			RequestMessage message= (RequestMessage) parser.parse(handshake);
			
			//Getting IP from handshake message
			String IPString = (String) message.getParameter("IP");
			String portString = (String) message.getParameter("PORT");
			
			//Converting
			InetAddress IP= InetAddress.getByName(IPString);
			int port= Integer.parseInt(portString);
			
			//Asking for connection
			client_messages=new Socket(IP, port);
			
			//Repeat until the client is connected
			while(true) {
				try {
					//Reading client message
					String request = control_in.readUTF();
					
					//Debug print
					System.out.println("received: "+request);
					
					//Executing client request
					executeRequest(request,control_out);
					
					//se un thread che ha avviato un canale di notifica puo' terminare
					//if(isNotificationThread)
					//	break;
				}
				//client closes connection
				catch(EOFException e) {
					System.out.println("Connection closed by client");
					break;
				}
				catch (IOException e1) {
					System.out.println("IO Exception while reading client message");
				}
			}
			
		} 
		catch (IOException e1) {
			System.out.println("IO Exception while instantiating client connection");
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//Closing 
		finally {
			//if(!isNotificationThread)
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


	private void executeRequest(String request, DataOutputStream out) {
		// TODO Auto-generated method stub
		try {
			//Creation reply message
			ResponseMessage reply;
			
			//Parsing JSON message
			JSONParser parser= new JSONParser();
			RequestMessage message= (RequestMessage) parser.parse(request);
			
			//Getting operation from JSONObject
			Operation op = (Operation) message.getParameter("OPERATION");
			
			//Validating operation field
			if(op==null) {
				System.out.println("Invalid operation type recived");
				reply= new ResponseMessage();
				reply.setParameters("OPERATION:ERR","BODY:Invalid request received");
			}
			
			
			switch(op) {
				case REGISTER:
					reply=registerUser(message, client_control);
					break;
				case CHAT_ADDING:
					reply=addToChat(message);
					break;
				case CHAT_CLOSING:
					reply=closeChat(message);
					break;
				case CHAT_CREATION: 
					reply=createChat(message);
					break;
				case CHAT_LISTING:
					reply=listChat(message);
					break;
				case FILE_TO_FRIEND:
					reply=fileToFriend(message);
					break;
				case FRIENDSHIP:
					reply=friendship(message);
					break;
				case LIST_FRIENDS:
					reply=listFriends(message);
					break;
				case LOGIN:
					reply=login(message, client_control, client_messages);
					break;
				case LOGOUT:
					reply=logout(message);
					break;
				case LOOKUP:
					reply=lookup(message);
					break;
				case MSG_TO_CHATROOM:
					reply=msgToChatroom(message);
					break;
				case MSG_TO_FRIEND:
					reply=msgToFriend(message);
					break;
				default:
					break;
			}
		} catch(ParseException e) {
			
		} catch(IllegalArgumentException e) {
			
		}
		
		//Sending reply to client
	}

	private ResponseMessage msgToChatroom(RequestMessage message) {
		//Creation reply message (conterr� ip e porta del destinatario)
		ResponseMessage reply= new ResponseMessage();
		
		return reply;
	}

	private ResponseMessage listFriends(RequestMessage message) {
		//Creation reply message (conterr� ip e porta del destinatario)
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
		//Creation reply message (conterr� ip e porta del destinatario)
		ResponseMessage reply= new ResponseMessage();
		NotificationMessage notify_friendship;
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//To network nodes
		User sender_user=usersbyname.get(sender);
		User receiver_user=usersbyname.get(receiver);
		
		synchronized(receiver_user) {
			if(receiver_user.isOnline()) {
				network.addEdge(sender_user, receiver_user);
				notify_friendship= new NotificationMessage();
				notify_friendship.setParameters("OPERATION:NOTIFY_FRIENDSHIP", "SENDER:"+sender);
			}
			
		}
		
		return reply;
		//Estraggo RMI da receiver_user
		//Chiamo il metodo NotifyEvent(notify_frienship)
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
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
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
		return reply;
	}

	private ResponseMessage msgToFriend(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		String body= (String) message.getParameter("BODY");
		
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
			reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
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
		
		//Setting reply
		reply.setParameters("OPERATION:OK"); 
		
		//Sending message
		if(!sendMessage(message, sender_user, receiver_user)) {
			reply.setParameters("OPERATION:ERR");
		}
		return reply;
	}

	/*
	 * Sappiamo gia che sono amici e che entrambi esistono
	 * TRADUCE IL MESSAGGIO secondo la lingua di receiver
	 * Se receiver � online, glie lo manda e restituisce true
	 * Altrimenti restituisce falso
	 */
	/**
	 * REQUIRES: 
	 * 	-	sender_user and receiver user are friends
	 * The function translates the message into the receiver language
	 * If receiver_user is online, the message is sent
	 * - Translate 
	 * @param sender_user
	 * @param receiver_user
	 * @param message
	 * @return true if the message is sent, false otherwise
	 */
	private boolean sendMessage(RequestMessage message, User sender_user, User receiver_user) {
		return message_manager.sendMessage(message, sender_user, receiver_user);
	}

	private ResponseMessage lookup(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("SENDER");
		
		//Setting reply
		reply.setParameters("OPERATION:OK");
		if(usersbyname.containsKey(username)) reply.setParameters("BODY:"+true);
		else reply.setParameters("BODY:"+false);
		return reply;
	}

	private ResponseMessage logout(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("SENDER");
		
		//Getting user
		User user=usersbyname.get(username);

		//Setting offline
		user.setOffline();
		
		reply.setParameters("OPERATION:OK");
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
	private ResponseMessage login(RequestMessage message, Socket client_control, Socket client_messages) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String username= (String) message.getParameter("SENDER");
		String password= (String) message.getParameter("PASSWORD");
		
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
		
		//Setting user online
		user.setOnline(client_control, client_messages);
		
		//Setting reply
		reply.setParameters("OPERATION:OK");
		
		//Adding user to PrivateMessageManager for future message requests
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
			if(chatroom.isPartecipant(usersbyname.get(username)))
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
		String chatroom= (String) message.getParameter("CHATROOM");
		
		//Is the sender a user?
		/*if(!usersbyname.containsKey(username)) 
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
		else */
		
		//If the chatroom doesn't exists
		if(!chatrooms.containsKey(chatroom)) {
			reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
			return reply;
		}
			
		//Is the user an administrator?
		if(!chatrooms.get(chatroom).deleteChatroom(usersbyname.get(username))) {
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not an administrator");
			return reply;
		} 
		
		//else
		reply.setParameters("OPERATION:OK");
		chatrooms.remove(chatroom);
		return reply;
	}

	private ResponseMessage addToChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String chatroom= (String) message.getParameter("CHATROOM");
		
		//Is the sender is not a user
		/*if(!usersbyname.containsKey(username)) 
			reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
			return reply;*/
		
		//If chatroom doesn't exist
		if(!chatrooms.containsKey(chatroom)) { //Does chatroom exist?
			reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
			return reply;
		}

		//Adding user to chatroom
		chatrooms.get(chatroom).addPartecipant(usersbyname.get(username));
		reply.setParameters("OPERATION:OK");
		return reply;
	}

	private ResponseMessage registerUser(RequestMessage message, Socket client) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("SENDER");
		String password= (String) message.getParameter("PASSWORD");
		String language= (String) message.getParameter("LANGUAGE");
		
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
			
			//Setting online user
			new_user.setOnline(client_control, client_messages);
			
			//Setting up reply
			reply.setParameters("OPERATION:OK");
			return reply;
		}catch(IllegalArgumentException e) {
			reply.setParameters("OPERATION:ERR","BODY:Invalid parameters");
			return reply;
		}
	}
}
