package communication;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * 
 * @author Francesco Sabiu
 * @author Marco Cardia
 *
 */
public interface RMIClientInterface extends Remote {
	
	/**
	 * Notify to all friends that the user 'friend' is now online 
	 * @param friend: user that is now online
	 * @throws RemoteException
	 */
	public void notifyOnlineFriend(String friend) throws RemoteException;
	
	/**
	 * Notify to all friends that the user 'friend' is now offline 
	 * @param friend: user that is now offline
	 * @throws RemoteException
	 */
	public void notifyOfflineFriend(String friend) throws RemoteException;
	
	/**
	 * Notify new friendship
	 * @param new_friend: user that needs to be notified
	 */
	public void newFriendship(String username) throws RemoteException;
	
	/**
	 * Notify to chatroom members that there is a new subscriber
	 * @param new_subscriber: new member of the chatroom
	 * @throws RemoteException
	*/
	public void newChatroomSubscriber(String new_subscriber, String chatroom) throws RemoteException;
	
	/**
	 * Notify to chatroom members that chatroom is now closed
	 * @param chatroom chatroom to be removed
	 * @throws RemoteException RMI protocol error
	 */
	public void closeChatroom(String chatroom) throws RemoteException;

	/**
	 * Notify to all online users that chatroom has been created
	 * @param chatroom
	 * @throws RemoteException 
	 */
	public void newChatroom(String chatroom) throws RemoteException;
}
