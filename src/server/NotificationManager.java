package server;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIClientInterface;
import util.Graph;

public class NotificationManager {
	private ConcurrentHashMap<String, User> usersbyname;
	private Graph<User> network;
	
	public NotificationManager(ConcurrentHashMap<String, User> usersbyname, ConcurrentHashMap<String, Chatroom> chatrooms, Graph<User> network) {
		this.usersbyname=usersbyname;
		this.network=network;
	}
	
	public void notifyChatroomJoin(User user, Chatroom chatroom) {
		//Getting chatroom participants
		HashSet<User> participants; 
		
		synchronized(chatroom) {
			participants=chatroom.getParticipants();
		}

		RMIClientInterface RMIChannel; 
		for(User participant: participants) {
			//Notify
			if(participant!=user && participant.isOnline()) {
				RMIChannel=participant.getRMIChannel();
				if(RMIChannel!=null) {//if participant is actually online
					try {
						RMIChannel.newChatroomSubscriber(user.getUsername(), chatroom.getName());
					} catch (RemoteException e) {
						System.out.println("Cannot notify to user");
					}
				}
			}
			
		}
	}

	public void notifyChatroomClosing(Chatroom chatroom) {
		//Getting chatroom name
		String chatroom_name=chatroom.getName();
		
		//Notifying 
		RMIClientInterface RMIChannel; 
		for(User u : usersbyname.values()) {
			//Notify
			RMIChannel=u.getRMIChannel();
			if(u.isOnline()) {//if is online
				try {
					RMIChannel.closeChatroom(chatroom_name);
				} catch (RemoteException e) {
					System.out.println("Cannot notify to "+u.getUsername());
				}
			}
		}
	}
	
	public void notifyFriendship(User sender, User receiver) {
		//Getting RMI Channel
		RMIClientInterface RMIChannel;
		RMIChannel= receiver.getRMIChannel();
		//Notify
		if(RMIChannel!=null) {//if is online
			try {
				RMIChannel.newFriendship(sender.getUsername());
			} catch (RemoteException e) {
				System.out.println("Cannot notify to user");
			}
		}
	}
	
	public void notifyOnlineFriend(User user) {
		String sender=user.getUsername();
		RMIClientInterface RMIChannel;
		Set<User> friends= network.getAdjVertices(user);
		
		synchronized(friends) {
			for(User friend: friends) {
				RMIChannel= friend.getRMIChannel();
				if(RMIChannel!=null) {//if receiver is online
					try {
						RMIChannel.notifyOnlineFriend(sender);
					} catch (RemoteException e) {
						System.out.println("Cannot notify to user");
					}
				}
			}
		}
	}
	
	public void notifyOfflineFriend(User user) {
		String sender=user.getUsername();
		RMIClientInterface RMIChannel;
		Set<User> friends= network.getAdjVertices(user);
		
		synchronized(friends) {
			for(User friend: friends) {
				System.out.println("Invio notifica a "+friend.getUsername());
				RMIChannel= friend.getRMIChannel();
				if(RMIChannel!=null) {//if receiver is online
					try {
						RMIChannel.notifyOfflineFriend(sender);
					} catch (RemoteException e) {
						System.out.println("Cannot notify to user");
					}
				}
			}
		}
	}
	
	public void notifyNewChatroom(Chatroom new_chatroom) {
		//Getting RMI Channel
		RMIClientInterface RMIChannel;
		//Notify
		for(User u : usersbyname.values()) {
			if(u.isOnline()) {
				RMIChannel= u.getRMIChannel();
				if(RMIChannel!=null) {//if user is online
					try {
						RMIChannel.newChatroom(new_chatroom.getName());
					} catch (RemoteException e) {
						System.out.println("Cannot notify to user");
					}
				}
			}
		}
	}
}
