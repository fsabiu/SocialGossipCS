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
	private HashSet<User> participants;
	private User creator;
	
	// Configuration properties
	private InetAddress msAddress; //Multicast socket IP address
	private String msName;
	private int msPort; //Multicast port
	
	private transient MulticastSocket socket;
	
	//Chatroom message dispatcher thread
	private transient ChatroomManager dispatcher;
	
	/**
	 * 
	 * @param name: name of the chatroom
	 * @param u: creator user
	 */
	public Chatroom(String name, User u, String msName, InetAddress msAddress) throws Exception {
		this.administrators= new HashSet<User>();
		this.participants= new HashSet<User>();
		this.name=name;
		this.administrators.add(u);
		this.participants.add(u);
		this.creator = u;
		
		//Multicast configuration
		this.msName = msName;
		this.msAddress=msAddress;
		this.msPort=PortScanner.freePort();
		if(msPort == -1) throw new Exception();
		
		this.socket = new MulticastSocket(msPort);
		System.out.println("Porta libera per "+this.name+": "+msPort);
		
		//Listener configuration
		dispatcher= new ChatroomManager(socket,msAddress);
		
	}
	
	/**
	 * The method adds the user u to this chatroom
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public synchronized void addParticipant(User u) {
			if(participants.contains(u)) throw new IllegalArgumentException();
			else this.participants.add(u);
	}
	
	/**
	 * The 
	 * @param u
	 * @throws IllegalArgumentException
	 */
	public synchronized void removeParticipant(User u) {
		if(!participants.contains(u)) throw new IllegalArgumentException();
		else this.participants.remove(u);
	}
	
	/**
	 * 
	 * @param u
	 * @return true if the user u belongs to this chatroom,
	 * 			false otherwise
	 */
	public synchronized boolean isParticipant(User u) {
		if(participants.contains(u)) return true;
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
	
	public synchronized boolean deleteChatroom(User u) {
		if(!isAdministrator(u)) return false;
		//TODO Notification to all users
		return true;
	}
	
	public synchronized String getName() {
		return this.name;
	}
	
	public ChatroomManager getDispatcher() {
		return this.dispatcher;
	}
	
	public InetAddress getAddress(){
		return this.msAddress;
	}
	
	public String getMsName() {
		return this.msName;
	}
	
	public String getIPAddress() {
		return msAddress.toString().replaceAll("[^\\d.]","");
	}
	
	public synchronized Integer getPort() {
		return new Integer(msPort);
	}

	public synchronized HashSet<User> getParticipants() {
		return this.participants;
	}
	
	public User getCreator() {
		return this.creator;
	}
}
