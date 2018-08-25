package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import communication.Message;
import communication.RMIClientInterface;
import communication.RMIServerInterface;
import communication.RequestMessage;
import communication.ResponseMessage;

public class MessageListener extends Thread{

	private MessageSender message_sender;
	private DataInputStream control_in;
	private ConcurrentHashMap<String,GUI> interfaces;
	private RMIServerInterface serverRMI;
	private RMIClientInterface callback;
	
	//It is used to receive Objects over socket
	private ObjectInputStream object_control_in = null;
	
	public MessageListener(DataInputStream control_in, MessageSender message_sender, ConcurrentHashMap<String,GUI> interfaces, RMIServerInterface serverRMI, RMIClientInterface callback) throws IOException {
		this.interfaces = interfaces;
		this.message_sender = message_sender;
		this.control_in = control_in;
		this.serverRMI = serverRMI;
		this.callback = callback;
		object_control_in = new ObjectInputStream(control_in);
	}

	public void run() {
		Object received_message;
		while(true) {
			System.out.println("In attesa di risposta");
			//Receiving request
			received_message=receiveResponse();
			if (received_message instanceof RequestMessage) {
				//TODO request
			} else if(received_message instanceof ResponseMessage) {
				ResponseMessage response = (ResponseMessage) received_message;
				System.out.println("Il messaggio ricevuto è "+response.toString());
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
	
	public void checkResponse(ResponseMessage reply) {
		//
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
					/*SocialGossipHomeGUI sgGUI;
					sgGUI=((LoginGUI) loginGUI).createSGHome(user);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);*/
					
					SocialGossipHomeGUI sgGUI = new SocialGossipHomeGUI( ((LoginGUI) interfaces.get("loginGUI")).getFrame(),user);
					sgGUI.setVisible(true);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);

					try {
						serverRMI.registerUserRMIChannel(user, callback);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			break;
			case "LOGOUT":{
					
				//Getting response from server
				//ResponseMessage response=checkResponse();

				if (reply.getParameter("OPERATION").equals("OK")) {
					//Opening chat interface to user
					/* Replaced by hashmap
					 * ((SocialGossipHomeGUI) gui).logoutGUI();
					 */
					String username = (String) reply.getParameter("SENDER");
					try {
						serverRMI.unregisterUserRMIChannel(username, callback);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).logoutGUI();
					System.out.println("Logout utente");
					
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
				System.out.println("Inviata richiesta di amicizia all'utente");				
				JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
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
					System.out.println("["+sender+":] "+reply.getParameter("BODY"));
					chatGUI.setConversationArea("["+sender+":] "+reply.getParameter("BODY"));
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
				System.out.println(reply.getParameter("OPERATION").equals("OK"));
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
					System.out.println(reply.getParameter("PORT"));

					create_chatroom(reply);
					//((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).getSelectedListChatroom();
				}
			}
			break;
			default: {
				JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
			}
			break;
		}
	}
		/*String op= (String) reply.getParameter("OPERATION");
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
	}*/

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
			e.printStackTrace();
		} 
		return comeON;
	}
}
