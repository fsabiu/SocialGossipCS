package server;

import communication.Message;

/**
 * Represent the interface of the manager of messages
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public interface MessageManager {
	/**
	 * Translate the message and sends it to the specified receiver
	 * @param message
	 * @param sender
	 * @param receiver
	 * @return
	 */
	public boolean sendMessageToUser(Message message, User receiver);
	
}
