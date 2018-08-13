package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import communication.Message;
import communication.RequestMessage;
import communication.ResponseMessage;

public class MessageListener extends Thread{

	private MessageSender message_sender;
	private DataInputStream control_in;
	private ConcurrentHashMap<String,GUI> interfaces;
	
	public MessageListener(DataInputStream control_in, MessageSender message_sender, ConcurrentHashMap<String,GUI> interfaces) {
		this.interfaces = interfaces;
		this.message_sender=message_sender;
		this.control_in=control_in;
	}

	public void run() {
		Message received_message;
		while(true) {
			//Receiving request
			received_message=receiveResponse();
			ResponseMessage response;
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
				//Showing the result to user
				((LoginGUI) interfaces.get("loginGUI")).getLoginResponse().setText((String) reply.getParameter("BODY"));
				
				if (reply.getParameter("OPERATION").equals("OK")) {
					
					interfaces.get("loginGUI").setVisible(false);
					
					String user = (String) reply.getParameter("USER");
					SocialGossipHomeGUI sgGUI = new SocialGossipHomeGUI( ((LoginGUI) interfaces.get("loginGUI")).getFrame() ,user);
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
					
					((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).setListFriends((String) reply.getParameter("BODY"));
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

	public ResponseMessage receiveResponse() {
		String replyString= null;
		try {
			//replyString= control_in.readUTF();
			replyString = DataInputStream.readUTF(control_in);
		} catch (IOException e) {
			System.out.println("In attesa di response");
			e.printStackTrace();
		}
		if (replyString == null) System.out.println("La risposta è nulla");
		
		ResponseMessage reply=new ResponseMessage();
		
		// Mi serve sapere che tipo è, dovrei leggere il json?
		reply.parseToMessage(replyString);
		return reply;
	}
}
