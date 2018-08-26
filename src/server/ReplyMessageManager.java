package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import communication.Message;

public class ReplyMessageManager implements MessageManager {

	public ReplyMessageManager() {
		
	}
	/**
	 * The methods sends a message to the specified receiver
	 */
	@Override
	public boolean sendMessageToUser(Message message, User receiver) {
		if(receiver.isOnline()) {
			//Getting receiver output stream
			try {
				//Sending
				(receiver.getControlOutputStream()).writeObject(message);
				return true;
			}catch(IOException e) {
				return false;
			}
		} else return false;
	}

	public boolean sendReply(Message message, ObjectOutputStream control_out){
		try {
			//Sending
			System.out.println("Reply to user: "+message.toString());
	        control_out.writeObject(message);
			return true;
		}catch(IOException e) {
			return false;
		}
	}
}
