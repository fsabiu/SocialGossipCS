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

public class PrivateMessageManager {
	User sender;
	
	public PrivateMessageManager() {
		this.sender=null;
	}
	
	private JSONObject translateMessage(Message message, String from, String to) {
		return message.translate(from, to);
	}
	
	public boolean sendMessage(Message message, User sender, User receiver) throws IllegalArgumentException {
		if (this.sender!=sender) throw new IllegalArgumentException();
		if(receiver.isOnline()) {
			String mess=translateMessage(message, sender.getLanguage(), receiver.getLanguage()).toJSONString();
			//Getting receiver output stream
			try {
				DataOutputStream control_out = new DataOutputStream(receiver.getMessagesSocket().getOutputStream());
				//Sending
				control_out.writeUTF(mess);
				return true;
			}catch(IOException e) {
				return false;
			}
		} else return false;
	}
	
	public void setSender(User sender) {
		this.sender=sender;
	}
	
	/**
	 * REQUIRES: sender and receiver are both users and friends.
	 * @param message
	 * @return
	 */
	public ResponseMessage getReceiverFileSocket(RequestMessage message, User sender, User receiver) {
		sendMessage(message, sender, receiver);
		ResponseMessage received= receiveMessage(receiver);
		return received;
	}
	
	public ResponseMessage receiveMessage(User sender) {
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
