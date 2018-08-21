package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import communication.Message;
import communication.RequestMessage;
import communication.ResponseMessage;

public class MessageListener extends Thread{

	private MessageSender message_sender;
	private DataInputStream control_in;
	private ConcurrentHashMap<String,GUI> interfaces;
	
	//It is used to receive Objects over socket
	private ObjectInputStream object_control_in = null;
	
	public MessageListener(DataInputStream control_in, MessageSender message_sender, ConcurrentHashMap<String,GUI> interfaces) throws IOException {
		this.interfaces = interfaces;
		this.message_sender=message_sender;
		this.control_in=control_in;
		object_control_in = new ObjectInputStream(control_in);
	}

	public void run() {
		Object received_message;
		while(true) {
			System.out.println("In attesa di risposta");
			//Receiving request
			received_message=receiveResponse();
			/*ResponseMessage response;
			String type = (String) received_message.getParameter("TYPE");
			switch(type) {
				case "request":{
					//TODO request
				}
				break;
				case "response":{
					response = (ResponseMessage) received_message;
					if (received_message instanceof ResponseMessage) System.out.println("ok");
					System.out.println(received_message.getClass());
					checkResponse(response);
				}
				break;
			}*/
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
					
					/*String user = (String) reply.getParameter("USER");
					SocialGossipHomeGUI sgGUI;
					sgGUI=((LoginGUI) loginGUI).createSGHome(user);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);*/
					
					SocialGossipHomeGUI sgGUI = new SocialGossipHomeGUI( ((LoginGUI) interfaces.get("loginGUI")).getFrame(),(String) reply.getParameter("USER"));
					sgGUI.setVisible(true);
					interfaces.putIfAbsent("socialGossipHomeGUI", sgGUI);

					//Opening chat interface to user
					/*((LoginGUI) interfaces.get("loginGUI")).createSGHome((String) reply.getParameter("USER"));*/
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
					interfaces.get("socialGossipHomeGUI");
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
				
				//Setting username and operation field
				/*req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_add= ((SocialGossipHomeGUI) gui).getUserToAddField().getText();
				req.setParameters("RECEIVER:"+user_to_add);
				
				//Sending request to server
				sendRequest(req);*/
				
				//Getting response from server
				//ResponseMessage response=checkResponse();
				
				JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
			} 
			break;
			case "LIST_FRIENDS":{
				//TODO SWITCH CASE
				
				//RICHIEDO LA LISTA DI AMICI
				//Setting username and operation field
				/*RequestMessage second_req = new RequestMessage(username);
				second_req.setParameters("OPERATION:LIST_FRIENDS");
				
				//Sending request to server
				sendRequest(second_req);
				
				//Getting response from server
				ResponseMessage second_response=checkResponse();*/
				
				if (reply.getParameter("OPERATION").equals("OK")) {
					//Server returns an ArrayList of strings
					
					//((SocialGossipHomeGUI) gui).setListFriends((String) second_response.getParameter("BODY"));
					//String list_friends = (String) reply.getParameter("BODY");
					
					if (reply.getParameter("BODY") != null) {
						((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setListFriends((String) reply.getParameter("BODY"));
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
				((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).createChatGUI();
				
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
					
					//Verifico la porta nel quale sto inviando il messaggio
					System.out.println(reply.getParameter("PORT"));
					
					//Storie varie per i test sull'invio dei messaggi nella chatroom
					/*try {
						Integer port = new Integer((String) reply.getParameter("PORT"));
						MulticastSocket msocket = new MulticastSocket(port);
						InetAddress gruppo = InetAddress.getByName((String) reply.getParameter("INETADDRESS"));
						msocket.joinGroup(gruppo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					
				}
			}
			break;
			case "CHAT_ADDING": {
				if (reply.getParameter("OPERATION").equals("OK")) {
					JOptionPane.showMessageDialog(null, reply.getParameter("BODY"));
					System.out.println(reply.getParameter("PORT"));
					
					/*
					 * Test sulla documentazione java 
					 String msg = "Hello";
					 InetAddress group = InetAddress.getByName("228.5.6.7");
					 MulticastSocket s = new MulticastSocket(6789);
					 s.joinGroup(group);
					 DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(),
					                             group, 6789);
					*/
					
					//Storie varie per i test sull'invio dei messaggi nella chatroom
					/*try {
						Integer port = new Integer((String) reply.getParameter("PORT"));
						MulticastSocket msocket = new MulticastSocket(port);
						//msocket.send(receivedPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
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

	public Object receiveResponse() {
		/*String replyString= null;
		try {
			//replyString= control_in.readUTF();
			ObjectInputStream in = new ObjectInputStream(control_in);
			
	        Object comeON = in.readObject();
			//replyString = DataInputStream.readUTF(control_in);
			if (replyString == null) System.out.println("La risposta è nulla");
			if (comeON instanceof ResponseMessage) {
				System.out.println("È un response"+((ResponseMessage) comeON).toString());
				// Mi serve sapere che tipo è, dovrei leggere il json?
				//reply.parseToMessage(replyString);
				
				return (ResponseMessage) comeON;
			}
			else if(comeON instanceof RequestMessage) {
				System.out.println("È un request");
			}
			else if(comeON instanceof Message) {
				System.out.println("È un message");
			}
		} catch (IOException e) {
			System.out.println("In attesa di response");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;*/
        Object comeON = null;
		try {
			//in = new ObjectInputStream(control_in);
			comeON = object_control_in.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return comeON;
	}
}
