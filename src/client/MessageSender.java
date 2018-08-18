package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;

import communication.Operation;
import communication.RequestMessage;
import communication.ResponseMessage;

public class MessageSender {
	//private Socket server_control_socket;
	private Socket server_message_socket;
	private ConcurrentHashMap<String,GUI> interfaces;
	private String username;
	private String password;
	private String language;
	//private DataInputStream control_in;
	private DataOutputStream control_out;

	//public MessageSender(Socket server_control_socket, Socket server_message_socket, LoginGUI loginGUI) {
	public MessageSender(DataOutputStream control_out, Socket server_message_socket, ConcurrentHashMap<String,GUI> interfaces) {
		this.password="";
		//this.server_control_socket=server_control_socket;
		this.server_message_socket=server_message_socket;
		
		/*try {
			//control_in= new DataInputStream(new BufferedInputStream(server_control_socket.getInputStream()));
			control_out= new DataOutputStream(server_control_socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error creating Streams IN/OUT");
			e.printStackTrace();
		}*/
		this.interfaces=interfaces;
		this.control_out=control_out;
	
	}
	
	//public void run() {
		
		//Pressing login button
		/*loginGUI.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUsername();
				setPassword();
				sendRequest();
			}
		});*/
	
		//Pressing SigIn button
		/*loginGUI.getBtnSignIn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//NECESSARIO INVOCARE METODI ADD ACTION LISTENER PER OGNI BOTTONE
			}
		});*/
	//}
	
	/* HO CREATO UNA SUPERCLASSE GUI CHE HA COME ATTRIBUTO UN REQUESTMAKER, OVVERO
	 * UN GESTORE DI EVENTI. IN GENERALE, ALL'ATTIVAZIONE DI UN EVENTO, SI CHIAMA IL METODO
	 * EVENTS HANDLER, CHE GESTISCE L'EVENTO IMPOSTANDO I CAMPI DEL MESSAGGIO E INVIANDO LA 
	 * RICHIESTA RELATIVA.
	 * CIASCUNA INTERFACCIA GRAFICA POTRA' CHIAMARE IL METODO IN QUANTO L'OGGETTO REQUESTMAKER E' UN
	 * ATTRIBUTO PROTECTED
	 * COSA FARA IL METODO RUN? REQUEST MAKER POTREBBE NON ESSERE PIU UNA CLASSE CHE ESTENDE THREADS
	 * (L'INTERFACCIA PERO SI BLOCCA!!!)
	 * ALTRA SOLUZIONE: PER OGNI EVENTO CHIAMA RUN! 
	 * 
	 * TODO spostare tutti i checkResponse
	 */
	public void eventsHandler(GUI gui, String event) {
		RequestMessage req = new RequestMessage(username);
		switch(event) {
			case "REGISTER":{
				System.out.println("Inviata richiesta registrazione");
				
				//Setting up username
				if ((username=((RegistrationGUI) gui).getUsernameField().getText()).equals("")) {
					//Username is empty
					((RegistrationGUI) gui).getRegistrationReply().setText("Username field cannot be empty");
					break;
				}
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting up password
				password="";
				char[] pass=((RegistrationGUI) gui).getPasswordField().getPassword();
				for(char c: pass) {
					password=password+c;
				}
				System.out.println("Password: "+password);
				req.setParameters("PASSWORD:"+password);
				
				//Setting up
				language=(String) ((RegistrationGUI) gui).getComboBox().getSelectedItem();
				req.setParameters("LANGUAGE:"+language);
				System.out.println("Lingua: "+language);
				
				//Sending request
				sendRequest(req);
				
				//Printing the response --- NOW IN MessageListener
				/*ResponseMessage response=checkResponse();
				((RegistrationGUI) gui).getRegistrationReply().setText((String) response.getParameter("BODY"));*/
			}
			break;
			case "LOGIN":{
				System.out.println("Inviata richiesta login");
				//Setting username
				if ((username=((LoginGUI) gui).getUsernameField().getText()).equals("")) {
					//Username is empty
					((LoginGUI) gui).getLoginResponse().setText("Username field cannot be empty");
					break;
				}
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting password
				password="";
				char[] pass=((LoginGUI) gui).getPasswordField().getPassword();
				for(char c: pass) password=password+c;
				req.setParameters("PASSWORD:"+password);
				
				//Sending request to server
				sendRequest(req);
				
				//Getting result from server
				/*ResponseMessage response=checkResponse();
				
				//Showing the result to user
				((LoginGUI) gui).getLoginResponse().setText((String) response.getParameter("BODY"));
				
				if (response.getParameter("OPERATION").equals("OK")) {
					//Opening chat interface to user
					((LoginGUI) gui).createSGHome(username);
				}*/
				
				req.setParameters("OPERATION:LIST_FRIENDS");
				//Sending request to server
				sendRequest(req);
				
				req.setParameters("OPERATION:CHAT_LISTING");
				sendRequest(req);
			}
			break;
			case "LOGOUT":{
				System.out.println("Inviata richiesta logout");
				
				//Setting username
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Sending request to server
				sendRequest(req);
				
				//Getting response from server
				/*ResponseMessage response=checkResponse();

				if (response.getParameter("OPERATION").equals("OK")) {
					//Opening chat interface to user
					((SocialGossipHomeGUI) gui).logoutGUI();
					System.out.println("Logout utente");
				}*/
			}
			break;
			case "LOOKUP":{
				System.out.println("Inviata richiesta di ricerca dell'utente");
				
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_search= ((SocialGossipHomeGUI) gui).getUserSearchField().getText();
				req.setParameters("USERNAME:"+user_to_search);
				
				//Sending request to server
				sendRequest(req);
				
				//Getting response from server
				/*ResponseMessage response=checkResponse();
				
				if (response.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, response.getParameter("BODY"));
				}*/
				
				/*
				 * TODO Cancellare, test sui messaggi
				 * req.setParameters("OPERATION:MSG_TO_FRIEND");
				req.setParameters("RECEIVER:"+user_to_search);
				req.setParameters("BODY:"+"Ciao, sono felice");
				System.out.println(req);
				sendRequest(req);*/
			}
			break;
			case "FRIENDSHIP":{
				System.out.println("Inviata richiesta di amicizia all'utente");
				
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_add= ((SocialGossipHomeGUI) gui).getUserToAddField().getText();
				req.setParameters("RECEIVER:"+user_to_add);
				
				//Sending request to server
				sendRequest(req);
				
				//Getting response from server
				/*ResponseMessage response=checkResponse();
				
				JOptionPane.showMessageDialog(null, response.getParameter("BODY"));*/
				//TODO SWITCH CASE
				/*if (response.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, response.getParameter("BODY"));
					//System.out.println("L'utente è stato aggiunto"+response.getParameter("BODY"));
				}
				//Other user is offline, show error message
				else if (response.getParameter("OPERATION").equals("USER_OFFLINE")) {
					JOptionPane.showMessageDialog(null, response.getParameter("BODY"));
				}
				else if */
				
				//RICHIEDO LA LISTA DI AMICI
				//Setting username and operation field
				RequestMessage second_req = new RequestMessage(username);
				second_req.setParameters("OPERATION:LIST_FRIENDS");
				
				//Sending request to server
				sendRequest(second_req);
				
				//Getting response from server
				/*ResponseMessage second_response=checkResponse();
				
				if (second_response.getParameter("OPERATION").equals("OK")) {
					//Server returns an ArrayList of strings
					((SocialGossipHomeGUI) gui).setListFriends((String) second_response.getParameter("BODY"));
					
					//JSONArray friends= (JSONArray) second_response.getParameter("BODY");
					//((SocialGossipHomeGUI) gui).setListFriends(friends);
				}*/
			}
			break;
			case "STARTCHAT": {
				System.out.println("Apertura chat");
				//Getting friend name
				((SocialGossipHomeGUI) gui).getSelectedListFriend();
				
				//Creating chat to chat with the above friend
				((SocialGossipHomeGUI) gui).createChatGUI();
				
			}
			break;
			case "CHAT_CREATION":{
				System.out.println("Inviata richiesta di creazione all'utente");
				
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting chatroom name 
				String new_chatroom= ((SocialGossipHomeGUI) gui).getNewChatroomField().getText();
				req.setParameters("CHATROOM:"+new_chatroom);
				
				//Sending request to server
				sendRequest(req);
				
				// Asking for list of chatrooms
				req.setParameters("OPERATION:CHAT_LISTING");
				sendRequest(req);
			}
		}
	}
	
	/*public ResponseMessage checkResponse() {
		//Receiving request
		ResponseMessage reply=receiveResponse();
		String op= (String) reply.getParameter("OPERATION");
		switch(op) {  
			case "OK":
				System.out.println("Operazione avvenuta con successo");
				break;
			case "USER_ALREADY_EXISTS":
				System.out.println("Esiste già un utente con quel nome");
				break;
			case "PERMISSION_DENIED":
				//Stampa testo body dopo
				System.out.println("Operazione non permessa");
				break;
			case "INVALID_CREDENTIALS":
				System.out.println("Credenziali errate");
				break;
			case "ERR":
				System.out.println("Errore generico");
				System.out.println("Errore: "+reply.getParameter("BODY"));
				break;
		default:
			System.out.println("Operation "+op);
			break;
		}
		
		return reply;
	}*/
	
	/*public ResponseMessage receiveResponse() {
		String replyString= null;
		try {
			replyString= control_in.readUTF();
		} catch (IOException e) {
			System.out.println("In attesa di response");
			e.printStackTrace();
		}
		
		ResponseMessage reply=new ResponseMessage();
		reply.parseToMessage(replyString);
		return reply;
	}*/
	
	public void sendRequest(RequestMessage req) {
		try {
			control_out.writeUTF(req.toString());		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createRegistrationGUI() {
		RegistrationGUI registrationGUI = new RegistrationGUI(((LoginGUI) interfaces.get("loginGUI")).getFrame());
		interfaces.putIfAbsent("registrationGUI", registrationGUI);
	}
}
