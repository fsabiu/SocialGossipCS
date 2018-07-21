package client;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;

import communication.RMIClientInterface;

public class NotificationReceiver extends RemoteObject implements RMIClientInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotificationReceiver() {
		super();
	}
	
	@Override
	public void notifyOnlineFriend(String friend) throws RemoteException {
		// Show notification to GUI
		
	}

	@Override
	public void notifyOfflineFriend(String friend) throws RemoteException {
		// Show notification to GUI
		
	}

	@Override
	public void newFriendship(String username) throws RemoteException {
		// Show notification to GUI
		
	}

	@Override
	public void newChatroomSubscriber(String new_subscriber, String chatroom) throws RemoteException {
		// Show notification to GUI
		
	}

	@Override
	public void closeChatroom(String chatroom) throws RemoteException {
		// Show notification to GUI
		
	}

}
