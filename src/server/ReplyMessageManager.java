package server;

import java.io.DataOutputStream;
import java.io.IOException;

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
			String mess=message.toString();
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

	public boolean sendReply(Message message, DataOutputStream control_out){
		try {
			//Sending
			control_out.writeUTF(message.toString());
			return true;
		}catch(IOException e) {
			return false;
		}
	}
}