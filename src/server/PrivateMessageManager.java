package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
	private JSONObject translateMessage(Message message, String from, String to) {
		return message.translate(from, to);
	}
	
	/**
	 * It translates the message and sends it through receiver messagesSocket socket
	 * @param message
	 * @param sender
	 * @param receiver
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean sendMessageToUser(Message message, User receiver) throws IllegalArgumentException {
		if(receiver.isOnline()) {
			String mess=translateMessage(message, sender.getLanguage(), receiver.getLanguage()).toJSONString();
			//Getting receiver output stream
			try {
				DataOutputStream messages_out = new DataOutputStream(receiver.getMessagesSocket().getOutputStream());
				//Sending
				messages_out.writeUTF(mess);
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
			String mess=translateMessage(message, sender.getLanguage(), receiver.getLanguage()).toJSONString();
			//Getting receiver output stream
			try {
				DataOutputStream control_out = new DataOutputStream(receiver.getControlSocket().getOutputStream());
				//Sending
				control_out.writeUTF(mess);
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
	public ResponseMessage getReceiverFileSocket(RequestMessage message, User sender, User receiver) {
		sendRequestToUser(message, receiver);
		//Get response message from receiver
		ResponseMessage received= receiveControlMessage(receiver);
		return received;
	}
	
	/**
	 * The method receives a control message by the specified sender
	 * @param sender
	 * @return
	 */
	public ResponseMessage receiveControlMessage(User sender) {
		try {
			DataInputStream control_in = new DataInputStream(sender.getControlSocket().getInputStream());
			
			//Receiving and parsing
			JSONParser parser= new JSONParser();
			ResponseMessage reply= (ResponseMessage) parser.parse(control_in.readUTF());
			return reply;
		}catch(IOException | ParseException e) {
			return null;
		}
	}
}
