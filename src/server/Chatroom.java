package server;

import java.util.HashSet;

/**
 * Class which represents the chatroom of SocialGossipCS
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class Chatroom {
	// Properties 
	private final String name;
	private final HashSet<User> administrators;
	private HashSet<User> partecipants;
	
	// Configuration properties
	private static final int PORT= 6000;
	
	/**
	 * 
	 * @param name: name of the chatroom
	 * @param u: creator user
	 */
	public Chatroom(String name, User u) {
		this.administrators= new HashSet<User>();
		this.partecipants= new HashSet<User>();
		this.name=name;
		this.administrators.add(u);
		this.partecipants.add(u);
	}
	
	/**
	 * The method adds the user u to this chatroom
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public void addPartecipant(User u) {
			if(partecipants.contains(u)) throw new IllegalArgumentException();
			else this.partecipants.add(u);
	}
	
	/**
	 * The 
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public void removePartecipant(User u) {
		if(!partecipants.contains(u)) throw new IllegalArgumentException();
		else this.partecipants.remove(u);
	}
	
	/**
	 * 
	 * @param u
	 * @return true if the user u belongs to this chatroom,
	 * 			false otherwise
	 */
	public boolean isPartecipant(User u) {
		if(partecipants.contains(u)) return true;
		else return false;
	}
	
	/**
	 * 
	 * @param u
	 * @return true if the user u is an administrator of this chatroom,
	 * 			false otherwise
	 */
	public boolean isAdministrator(User u) {
		if(administrators.contains(u)) return true;
		else return false;
	}
}
