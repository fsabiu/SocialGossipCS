package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UTFDataFormatException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.NotificationMessage;
import communication.Operation;
import communication.RequestMessage;
import communication.ResponseMessage;

import org.json.*;

/**
 * Client request manager class implementation
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class RequestManager implements Runnable {
	private Socket client;
	private Graph<User> network;
	private ConcurrentHashMap<String, Chatroom> chatrooms;
	private ConcurrentHashMap<String, User> usersbyname;
		
	/**
	 * Constructor
	 * @param client
	 * @param network
	 * @param chatrooms
	 * @param usersbyname
	 * @throws IllegalArgumentException
	 */
	public RequestManager(Socket client,Graph<User> network, ConcurrentHashMap<String, Chatroom> chatrooms, ConcurrentHashMap<String, User> usersbyname) throws IllegalArgumentException {
		super();
		if(client==null || network==null || chatrooms==null || usersbyname==null) {
			throw new IllegalArgumentException();
		}
		this.client=client;
		this.network=network;
		this.chatrooms=chatrooms;
		this.usersbyname=usersbyname;
	}
	
	@Override
	public void run() {
		
		//Declaring stream addresses
		DataInputStream in = null;
		DataOutputStream out = null;
		
		
		try {
			in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
			out = new DataOutputStream(client.getOutputStream());
			
			//Repeat until the client is connected
			while(true) {
				try {
					//Reading client message
					String request = in.readUTF();
					
					//Debug print
					System.out.println("received: "+request);
					
					//Executing client request
					executeRequest(request,out);
					
					//se un thread che ha avviato un canale di notifica puo' terminare
					//if(isNotificationThread)
					//	break;
				}
				//client ha chiuso la connessione
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
			System.out.println("IO Exception while nstantiating client connection");
		}
		
		//Closing 
		finally {
			//if(!isNotificationThread)
			//{
				try {
					if(client != null) client.close();
					if(in != null) in.close();
					if(out != null) out.close();
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
				reply.setParameters("OPERATION:ERR","BODY:Invalid request received");
			}
			
			
			switch(op) {
				case REGISTER:
					reply=registerUser(message);
					break;
				/*case UNREGISTER:
					break;
				*/
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
					reply=login(message);
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
		//Creation reply message (conterrà ip e porta del destinatario)
		ResponseMessage reply= new ResponseMessage();
		
		
	}

	private ResponseMessage listFriends(RequestMessage message) {
		//Creation reply message (conterrà ip e porta del destinatario)
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
		//Creation reply message (conterrà ip e porta del destinatario)
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
				notify_friendship.setParameters("OPERATION:NOTIFY_FRIENDSHIP", "USER:"+sender);
			}
			
		}
		
		return reply;
		//Estraggo RMI da receiver_user
		//Chiamo il metodo NotifyEvent(notify_frienship)
	}

	private ResponseMessage fileToFriend(RequestMessage message) {
		//Creation reply message (conterrà ip e porta del destinatario)
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String sender= (String) message.getParameter("SENDER");
		String receiver= (String) message.getParameter("RECEIVER");
		
		//Contatto il destinatario scrivendogli di aprire un socket per ricevere il file
		//Aspetto risposta del destinatario
		
		//Leggo risposta
		
		//La scrivo in Reply
		
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
				reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
				return reply;
			}
		}
		
		//
		reply.setParameters("OPERATION:OK"); 
		
		//Sending message
		if(!sendMessage(sender_user, receiver_user, String body)) {
			reply.setParameters("OPERATION:ERR");
		}
		return reply;
	}

	/*
	 * Sappiamo gia che sono amici e che entrambi esistono
	 * TRADUCE IL MESSAGGIO secondo la lingua di receiver
	 * Se receiver è online, glie lo manda e restituisce true
	 * Altrimenti restituisce falso
	 */
	private boolean sendMessage(User sender_user, User receiver_user, String body) {
		// TODO 
		return false;
	}

	private ResponseMessage lookup(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("USERNAME");
		
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
		String username= (String) message.getParameter("USERNAME");
		
		//Getting user
		User user=usersbyname.get(username);

		//Setting offline
		user.setOffline();
		
		reply.setParameters("OPERATION:OK");
		return reply;
	}

	private ResponseMessage login(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting relevant fields
		String username= (String) message.getParameter("USERNAME");
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

		//else
		usersbyname.get(username).setOnline();
		reply.setParameters("OPERATION:OK");
		return reply;
		}

	private ResponseMessage listChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Getting username
		String username= (String) message.getParameter("USERNAME");
		
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
		String username= (String) message.getParameter("USERNAME");
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
			
		//Creating new chatroom
		Chatroom new_chatroom= new Chatroom(chatroom, usersbyname.get(username));
		chatrooms.putIfAbsent(chatroom, new_chatroom);
		reply.setParameters("OPERATION:OK");
		return reply;
	}

	private ResponseMessage closeChat(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("USERNAME");
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
		String username= (String) message.getParameter("USERNAME");
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

	private ResponseMessage registerUser(RequestMessage message) {
		//Creation reply message
		ResponseMessage reply= new ResponseMessage();
		
		//Extracting relevant fields
		String username= (String) message.getParameter("USERNAME");
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
			
			//User is now online because of User constructor
			usersbyname.putIfAbsent(username, new_user);
			network.addVertex(new_user);
			
			//Setting up reply
			reply.setParameters("OPERATION:OK");
			return reply;
		}catch(IllegalArgumentException e) {
			reply.setParameters("OPERATION:ERR","BODY:Invalid parameters");
			return reply;
		}
	}
}





















