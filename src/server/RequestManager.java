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
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
			ResponseMessage reply= new ResponseMessage();
			
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
					try {
						//Extracting relevant fields
						String username= (String) message.getParameter("USERNAME");
						String password= (String) message.getParameter("PASSWORD");
						String language= (String) message.getParameter("LANGUAGE");
						
						//Validating parameters
						if(username==null || password==null || language== null) {
							throw new IllegalArgumentException();
						}
						
						//If user already exists
						if(usersbyname.containsKey(username)) {
							reply.setParameters("OPERATION:USER_ALREADY_EXISTS","BODY:Username already exists");
						}else {
							//Adding user to data structures
							User new_user= new User(username, password, language);
							//User is now online because of User constructor
							usersbyname.put(username, new_user);
							network.addVertex(new_user);
							
							//Setting up reply
							reply.setParameters("OPERATION:OK");
						}
					}catch(IllegalArgumentException e) {
						reply.setParameters("OPERATION:ERR","BODY:Invalid request format");
						System.out.println("Invalid request parameters received by the client");
					}
					break;
				case UNREGISTER:
					break;
				case CHAT_ADDING: {
						//Extracting relevant fields
						String username= (String) message.getParameter("USERNAME");
						String chatroom= (String) message.getParameter("CHATROOM");
						
						//Is the sender a user?
						/*if(!usersbyname.containsKey(username)) 
							reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
						else */if(!chatrooms.containsKey(chatroom)) { //Does chatroom exist?
							reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
							
						}
						else {
							chatrooms.get(chatroom).addPartecipant(usersbyname.get(username));
						}
					}
					break;
				case CHAT_CLOSING: {
						//Extracting relevant fields
						String username= (String) message.getParameter("USERNAME");
						String chatroom= (String) message.getParameter("CHATROOM");
						
						//Is the sender a user?
						/*if(!usersbyname.containsKey(username)) 
							reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
						
						else */if(!chatrooms.containsKey(chatroom)) //Does chatroom exist?
							reply.setParameters("OPERATION:CHATROOM_DOES_NOT_EXIST");
						
						else {//Is the user an administrator?
							if(!chatrooms.get(chatroom).deleteChatroom(usersbyname.get(username))) {
								reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not an administrator");
							} else {
								reply.setParameters("OPERATION:OK");
								chatrooms.remove(chatroom);
							}
						}
					}
					break;
				case CHAT_CREATION: {
						//Extracting relevant fields
						String username= (String) message.getParameter("USERNAME");
						String chatroom= (String) message.getParameter("CHATROOM");
						
						//Is the sender a user?
						/*if(!usersbyname.containsKey(username)) 
							reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
						
						else */if(chatrooms.containsKey(chatroom)) //Does chatroom already exist?
							reply.setParameters("OPERATION:CHATROOM_ALREADY_EXISTS");
						
						else {//Creating new chatroom
								Chatroom new_chatroom= new Chatroom(chatroom, usersbyname.get(username));
								reply.setParameters("OPERATION:OK");
							}
					}
					break;
				case CHAT_LISTING: {
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
				}
					break;
				case FILE_TO_FRIEND:
					break;
				case FRIENDSHIP:
					break;
				case LIST_FRIENDS:
					break;
				case LOGIN: {
					String username= (String) message.getParameter("USERNAME");
					String password= (String) message.getParameter("PASSWORD");
					if(!usersbyname.containsKey(username)) 
						reply.setParameters("OPERATION:PERMISSION_DENIED","BODY:Not a user");
					else {
						//CONTROLLO PASSWORD
						usersbyname.get(username).setOnline();
						reply.setParameters("OPERATION:OK");
					}
				}
					break;
				case LOGOUT: {
					String username= (String) message.getParameter("USERNAME");
					//chiudi questa connessione TCP
					//if(non ha altre connessioni)
					usersbyname.get(username).setOffline();
					
					//in ogni caso
					reply.setParameters("OPERATION:OK");
				}
					break;
				case LOOKUP: {
					String username= (String) message.getParameter("USERNAME");
					
					//Setting reply
					reply.setParameters("OPERATION:OK");
					if(usersbyname.containsKey(username)) reply.setParameters("BODY:"+true);
					else reply.setParameters("BODY:"+false);
				}
					break;
				case MSG_TO_CHATROOM:
					break;
				case MSG_TO_FRIEND:
					String sender= (String) message.getParameter("SENDER");
					String receiver= (String) message.getParameter("RECEIVER");
					
					//To network nodes
					User sender_user=usersbyname.get(sender);
					User receiver_user=usersbyname.get(receiver);
					
					//Is receiver a user?
					if(!usersbyname.containsKey(receiver)) {
						reply.setParameters("OPERATION:USER_DOES_NOT_EXIST", "BODY:Selected user does not exist");
						
					} else if(!network.areAdj(sender_user, receiver_user)) { //Are they friends?
						reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
						
					}else { //Is receiver online?
						boolean online;
						synchronized(receiver_user) {
							online=receiver_user.isOnline()
							if(!online)
							reply.setParameters("OPERATION:PERMISSION_DENIED", "BODY:You are not friends");
						}
						if(online) reply.setParameters("OPERATION:OK"); 
					}
					
					//chiamata invio messaggio, probabilmente user.sendMessage
					break;
				default:
					break;
			}
		} catch(ParseException e) {
			
		} catch(IllegalArgumentException e) {
			
		}
		
		//Sending reply to client
	}

}





















