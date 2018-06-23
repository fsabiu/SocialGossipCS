package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.Operation;
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
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} 
		catch (IOException e1) {
			e1.printStackTrace();
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
			JSONParser parser= new JSONParser();
			
			//Parsing JSON message
			JSONObject message= (JSONObject) parser.parse(request);
			//Getting operation from JSONObject
			Operation op = (Operation) message.get("OPERATION");
			
			//Creation reply message
			/**
			 * ResponseMessage reply= new ResponseMessage(); 
			 * Proposta modifica costruttore, farlo senza operazione per
			 * poterlo scrivere nello switch a seconda dei casi
			 */
			switch(op) {
				case REGISTER:
					//Extracting relevant fields
					String username= (String) message.get("USERNAME");
					String password= (String) message.get("PASSWORD");
					String language= (String) message.get("LANGUAGE");
					
					User new_user= new User(username, password, language);//Catturare IllegalArgument
					usersbyname.put(username, new_user);//Vedere se esiste già
					network.addVertex(new_user);//
					break;
				case UNREGISTER:
					break;
				case CHAT_ADDING:
					break;
				case CHAT_CLOSING:
					break;
				case CHAT_CREATION:
					break;
				case CHAT_LISTING:
					break;
				case ERR:
					break;
				case FILE_TO_FRIEND:
					break;
				case FRIENDSHIP:
					break;
				case LIST_FRIENDS:
					break;
				case LOGIN:
					break;
				case LOGOUT:
					break;
				case LOOKUP:
					break;
				case MSG_TO_CHATROOM:
					break;
				case MSG_TO_FRIEND:
					break;
				default:
					break;
			}
		}
		catch(ParseException | NullPointerException e) {
			
		}
	}

}





















