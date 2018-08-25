package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JOptionPane;

import communication.RMIClientInterface;

public class NotificationReceiver extends UnicastRemoteObject implements RMIClientInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String, GUI> interfaces;

	public NotificationReceiver(ConcurrentHashMap<String, GUI> interfaces) throws RemoteException {
		super();
		this.interfaces = interfaces;
	}
	
	@Override
	public void notifyOnlineFriend(String friend) throws RemoteException {
		// Show notification to GUI
		JOptionPane.showMessageDialog(null, "Il tuo amico "+friend+" è ora online");
	}

	@Override
	public void notifyOfflineFriend(String friend) throws RemoteException {
		// Show notification to GUI
		JOptionPane.showMessageDialog(null, "Il tuo amico "+friend+" è ora offline");
	}

	@Override
	public void newFriendship(String username) throws RemoteException {
		// Show notification to GUI
		JOptionPane.showMessageDialog(null, username+" ti ha aggiunto agli amici");
		((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).addFriendToList(username);
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
