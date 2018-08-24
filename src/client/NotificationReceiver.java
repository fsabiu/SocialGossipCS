package client;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;

import communication.RMIClientInterface;

public class NotificationReceiver extends UnicastRemoteObject implements RMIClientInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotificationReceiver() throws RemoteException {
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
		System.out.println("Aggiunto amico "+username);
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
