package server;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIClientInterface;

public class NotificationManager {
	private ConcurrentHashMap<String, Chatroom> chatrooms;
	private ConcurrentHashMap<String, User> usersbyname;
	
	public NotificationManager(ConcurrentHashMap<String, User> usersbyname, ConcurrentHashMap<String, Chatroom> chatrooms) {
		this.chatrooms=chatrooms;
		this.usersbyname=usersbyname;
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
			RMIChannel=participant.getRMIChannel();
			if(RMIChannel!=null) {//if is online
				try {
					RMIChannel.newChatroomSubscriber(participant.getUsername(), chatroom.getName());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyChatroomClosing(Chatroom chatroom) {
		// TODO Auto-generated method stub
		
	}
}
