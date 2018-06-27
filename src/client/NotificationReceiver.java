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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyOfflineFriend(String friend) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newFriendship(String username) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void newChatroomSubscriber(String new_subscriber, String chatroom) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChatroom(String chatroom) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
