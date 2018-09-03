package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import communication.RequestMessage;
import communication.ResponseMessage;

/**
 * Class used to receive an input from the interface and to send messages to server
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class MessageSender {
	private Socket server_message_socket;
	private ConcurrentHashMap<String,GUI> interfaces;
	private String username;
	private String password;
	private String language;

	private ObjectOutputStream control_out;
	private ObjectOutputStream message_out;

	public MessageSender(ObjectOutputStream control_out, ObjectOutputStream message_out, Socket server_message_socket, ConcurrentHashMap<String,GUI> interfaces) {
		this.password="";
		this.server_message_socket=server_message_socket;
		this.interfaces=interfaces;
		this.control_out=control_out;
		this.message_out=message_out;	
	}
	
	// Input received from the interfaces. It is coded as a string (event parameter)
	public void eventsHandler(GUI gui, String event) {
		RequestMessage req = new RequestMessage(username);
		switch(event) {
			case "REGISTER":{
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
				
				sendRequest(req);
			}
			break;
			case "LOGIN":{
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
			}
			break;
			case "LOGOUT":{
				//Setting username
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Sending request to server
				sendRequest(req);
			}
			break;
			case "LOOKUP":{
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_search= ((SocialGossipHomeGUI) gui).getUserSearchField().getText();
				req.setParameters("USERNAME:"+user_to_search);
				
				//Sending request to server
				sendRequest(req);
			}
			break;
			case "FRIENDSHIP":{
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting username to search
				String user_to_add= ((SocialGossipHomeGUI) gui).getUserToAddField().getText();
				req.setParameters("RECEIVER:"+user_to_add);
				
				//Sending request to server
				sendRequest(req);
				
				//Sending request for friend list  
				//Setting username and operation field
				RequestMessage second_req = new RequestMessage(username);
				second_req.setParameters("OPERATION:LIST_FRIENDS");
				
				//Sending request to server
				sendRequest(second_req);
			}
			break;
			case "STARTCHAT": {
				//Getting friend name
				String friend = ((SocialGossipHomeGUI) gui).getSelectedListFriend();
				if(friend!=null) {
					//Creating chat to chat with the above friend
					ChatGUI chatGUI;
					if (!interfaces.containsKey("chatGUI"+friend)) {
						chatGUI = new ChatGUI(friend);
						chatGUI.setVisible(true);
						interfaces.putIfAbsent("chatGUI"+friend, chatGUI);
					}
					else {
						chatGUI = (ChatGUI) interfaces.get("chatGUI"+friend);
						chatGUI.setVisible(true);
					}	
				}
			}
			break;
			case "STARTCHATROOM": {
				String chatroom = ((SocialGossipHomeGUI) gui).getSelectedListChatroom();
				if(chatroom != null) {
					ChatroomGUI chatroomGUI;
					if (interfaces.containsKey("chatroomGUI"+chatroom)) {
						interfaces.get("chatroomGUI"+chatroom).setVisible(true);
					}
					else {
						chatroomGUI = new ChatroomGUI(chatroom);
						chatroomGUI.setVisible(true);
						interfaces.putIfAbsent("chatroomGUI"+chatroom, chatroomGUI);
					}
				}
				else {
					JOptionPane pane = new JOptionPane("Seleziona una chatroom");
		            JDialog dialog = pane.createDialog(null, "Error");
		            dialog.setModal(false);
		            dialog.setVisible(true);
				}
			}
			break;
			case "MSG_TO_FRIEND": {
				// Sending request to friend
				String friend = ((ChatGUI) gui).getTitle();
				String msg = ((ChatGUI) gui).getTextArea().getText();
				req.setParameters("OPERATION:"+event,"RECEIVER:"+friend,"BODY:"+msg);
				sendRequest(req);
				
				// Sending message to friend
				RequestMessage req_msg = new RequestMessage(username);
				req_msg.setParameters("RECEIVER:"+friend,"BODY:"+msg);
				sendMessage(req_msg);
				((ChatGUI) gui).getTextArea().setText("");
			}
			break;
			case "CHAT_CREATION": {
				//Setting chatroom name 
				String new_chatroom= ((SocialGossipHomeGUI) gui).getNewChatroomField().getText();
				if (new_chatroom.equals("")) {
					JOptionPane.showMessageDialog(null, "Inserire il nome della chatroom");
					return;
				}
				//Setting username and operation fields
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				req.setParameters("CHATROOM:"+new_chatroom);
				
				//Sending request to server
				sendRequest(req);
			}
			break;
			case "CHAT_ADDING": {
				//Setting username and operation field
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting chatroom name
				String new_chatroom = ((SocialGossipHomeGUI) gui).getSelectedListChatroom();
				req.setParameters("CHATROOM:"+new_chatroom);
				
				sendRequest(req);
			}
			break;
			case "MSG_TO_CHATROOM": {
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				String msg = ((ChatroomGUI) gui).getTextArea().getText();
				req.setParameters("CHATROOM:"+((ChatroomGUI) gui).getTitle(),"BODY:"+msg);
				sendRequest(req);
				((ChatroomGUI) gui).getTextArea().setText("");
			}
			break;
			case "CHAT_CLOSING": {
				String chatroom = ((SocialGossipHomeGUI) gui).getSelectedListChatroom();
				if (chatroom!=null) {
					req.setParameters("SENDER:"+username,"OPERATION:"+event);
					//Setting chatroom name
					req.setParameters("CHATROOM:"+chatroom);
					sendRequest(req);
				}
				else {
					JOptionPane pane = new JOptionPane("Seleziona una chatroom");
		            JDialog dialog = pane.createDialog(null, "Error");
		            dialog.setModal(false);
		            dialog.setVisible(true);
				}
			}
			break;
			case "FILE_TO_FRIEND": {
				String fileName = ((ChatGUI) gui).getTextArea().getText();
				String receiver = ((ChatGUI) gui).getTitle();
				req.setParameters("SENDER:"+username,"OPERATION:"+event,"FILENAME:"+fileName,"RECEIVER:"+receiver);
				
				sendRequest(req);
				((ChatGUI) gui).getTextArea().setText("");
			}
			break;
		}
	}
	
	private void sendMessage(RequestMessage msg) {
		System.out.println("Invio "+msg);
		try {
			message_out.writeObject(msg);
		} catch (IOException e) {
			System.out.println("Error while writing in message_out stream");
		}
	}

	public void sendRequest(RequestMessage req) {
		try {
			control_out.writeObject(req);
		} catch (IOException e) {
			System.out.println("Error while writing in control_out stream for a request message");
		}
	}
	
	public void createRegistrationGUI() {
		RegistrationGUI registrationGUI = new RegistrationGUI(((LoginGUI) interfaces.get("loginGUI")).getFrame());
		interfaces.putIfAbsent("registrationGUI", registrationGUI);
	}

	public void sendResponse(ResponseMessage res) {
		try {
			control_out.writeObject(res);
		} catch (IOException e) {
			System.out.println("Error while writing in control_out stream for a response message");
		}
	}
}
