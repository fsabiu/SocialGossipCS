package server;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashSet;

import util.PortScanner;

/**
 * Class which represents the chatroom of SocialGossipCS
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class Chatroom {
	// Properties 
	private final String name;
	private HashSet<User> administrators;
	private HashSet<User> partecipants;
	
	// Configuration properties
	private InetAddress msAddress; //Multicast socket IP address
	private int msPort; //Multicast port
	private InetAddress listenAddress; //Dispatcher IP address
	private int listenPort;  //Dispatcher port
	
	private transient MulticastSocket socket;
	
	//Chatroom message dispatcher thread
	private transient ChatroomManager dispatcher;
	
	/**
	 * 
	 * @param name: name of the chatroom
	 * @param u: creator user
	 */
	public Chatroom(String name, User u,InetAddress msAddress,InetAddress listenAddress) throws Exception {
		this.administrators= new HashSet<User>();
		this.partecipants= new HashSet<User>();
		this.name=name;
		this.administrators.add(u);
		this.partecipants.add(u);
		
		//Multicast configuration
		this.msAddress=msAddress;
		this.msPort=PortScanner.freePort();
		if(msPort == -1) throw new Exception();
		
		this.socket = new MulticastSocket(msPort);
		
		//Listener configuration
		this.listenAddress=listenAddress;
		dispatcher= new ChatroomManager(socket,msAddress);
		this.listenPort = dispatcher.getListeningPort();
		
		//Starting Chatroom manager
		dispatcher.start();
	}
	
	/**
	 * The method adds the user u to this chatroom
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public synchronized void addPartecipant(User u) {
			if(partecipants.contains(u)) throw new IllegalArgumentException();
			else this.partecipants.add(u);
	}
	
	/**
	 * The 
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public synchronized void removePartecipant(User u) {
		if(!partecipants.contains(u)) throw new IllegalArgumentException();
		else this.partecipants.remove(u);
	}
	
	/**
	 * 
	 * @param u
	 * @return true if the user u belongs to this chatroom,
	 * 			false otherwise
	 */
	public synchronized boolean isPartecipant(User u) {
		if(partecipants.contains(u)) return true;
		else return false;
	}
	
	/**
	 * 
	 * @param u
	 * @return true if the user u is an administrator of this chatroom,
	 * 			false otherwise
	 */
	public synchronized boolean isAdministrator(User u) {
		if(administrators.contains(u)) return true;
		else return false;
	}
	
	/**
	 * 
	 * @param u
	 * @return
	 */
	public synchronized boolean deleteChatroom(User u) {
		if(!isAdministrator(u)) return false;
		//TODO Notification to all users
		return true;
	}
	
	public synchronized String getName() {
		return this.name;
	}
}
