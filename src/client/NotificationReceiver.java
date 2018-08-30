package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JDialog;
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
		JOptionPane pane = new JOptionPane("Il tuo amico "+friend+" è ora online");
		JDialog dialog = pane.createDialog(null, "Notification");
		dialog.setModal(false);
		dialog.setVisible(true);
	}

	@Override
	public void notifyOfflineFriend(String friend) throws RemoteException {
		// Show notification to GUI
		JOptionPane pane = new JOptionPane("Il tuo amico "+friend+" è ora offline");
		JDialog dialog = pane.createDialog(null, "Notification");
		dialog.setModal(false);
		dialog.setVisible(true);
		//JOptionPane.showMessageDialog(null, "Il tuo amico "+friend+" è ora offline");
	}

	@Override
	public void newFriendship(String username) throws RemoteException {
		// Show notification to GUI
		((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).addFriendToList(username);

		// Preparing chat interface with the new friend
		ChatGUI chatGUI = new ChatGUI(username);
		interfaces.putIfAbsent("chatGUI"+username, chatGUI);
	}

	@Override
	public void newChatroomSubscriber(String new_subscriber, String chatroom) throws RemoteException {
		// Show notification to GUI
		JOptionPane.showMessageDialog(null, new_subscriber+" si è inscritto alla chatroom "+chatroom);
	}

	@Override
	public void closeChatroom(String chatroom) throws RemoteException {
		// Show notification to GUI
		JOptionPane.showMessageDialog(null, "La chatroom "+chatroom+" è stata chiusa!");
		((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).removeChatroom(chatroom);
	}

	@Override
	public void newChatroom(String chatroom) throws RemoteException {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "La chatroom "+chatroom+" è stata creata!");
		((SocialGossipHomeGUI) interfaces.get("socialGossipHomeGUI")).addChatroom(chatroom);
	}

}
