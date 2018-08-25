package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import communication.Message;
import communication.RequestMessage;
import communication.ResponseMessage;

public class PrivateMessageManager implements MessageManager {
	User sender;
	
	public PrivateMessageManager() {
		this.sender=null;
	}
	
	/**
	 * It translates the message
	 * @param message
	 * @param from
	 * @param to
	 * @return
	 */
	private Message translateMessage(Message message, String from, String to) {
		if(!from.equals(to)) {
			return message.translate(from, to);
		}
		else return message;
	}
	
	/**
	 * It translates the message and sends it through receiver messagesSocket socket
	 * @param message
	 * @param sender
	 * @param receiver
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean sendMessageToUser(Message message, User receiver) {
		RequestMessage mess;
		if(receiver.isOnline()) {
			if(!sender.getLanguage().equals(receiver.getLanguage())) {
				mess= (RequestMessage) translateMessage(message, sender.getLanguage(), receiver.getLanguage());
			} else {
				mess= (RequestMessage) message;
			}
			System.out.println("Translated message: "+mess.toString());
			//Getting receiver output stream
			try {
				ObjectOutputStream messages_out = receiver.getMessageOutputStream();
				//Sending
				messages_out.writeObject(mess);
				return true;
			}catch(IOException e) {
				return false;
			}
		} else return false;
	}
	
	public void setSender(User sender) {
		this.sender=sender;
	}
	
	public boolean sendRequestToUser(Message message, User receiver) throws IllegalArgumentException {
		if(receiver.isOnline()) {
			//Getting receiver output stream
			try {
				ObjectOutputStream control_out = receiver.getControlOutputStream();
				//Sending
				control_out.writeObject(message);
				return true;
			}catch(IOException e) {
				return false;
			}
		} else return false;
	}
	
	
	public User getSender() {
		return this.sender;
	}
	/**
	 * REQUIRES: sender and receiver are both users and friends.
	 * @param message
	 * @return fileSock
	 */
	public boolean askReceiverFileSocket(RequestMessage message, User sender, User receiver) {
		System.out.println("Ho chiesto i dati NIO a "+receiver.getUsername());
		return sendRequestToUser(message, receiver);
//		//Get response message from receiver
//		ResponseMessage received= receiveControlMessage(receiver);
//		System.out.println("Ho ricevuto i dati NIO da "+receiver.getUsername());
	}
	
	/**
	 * The method receives a control message by the specified sender
	 * @param sender
	 * @return
	 */
//	public ResponseMessage receiveControlMessage(User sender) {
//		try {
//			System.out.println("Output stream di "+sender.getUsername()+": "+sender.getControlInputStream());
//			ObjectInputStream control_in = sender.getControlInputStream();
//			
//			//Receiving
//			ResponseMessage reply= (ResponseMessage)control_in.readObject();
//			return reply;
//		} catch(IOException | ClassNotFoundException e) {
//			return null;
//		}
//	}
}
