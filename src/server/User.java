package server;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.Socket;

import communication.RMIServerInterface;
import communication.RMIClientInterface;


/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 * The class represent a subscribed user, identified by unique username.
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Personal data
	private String username;
	private String password;
	private String language;
	private boolean online;
	
	//Managing connections
	private transient Socket control_socket=null;
	private transient Socket messages_socket=null;
	DataOutputStream out=null;
	
	//RMI Channel
	private RMIClientInterface RMIchannel = null;
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @param language
	 * @throws IllegalArgumentException
	 */
	public User(String username, String password, String language) throws IllegalArgumentException{
		//Parameters checking
		if(username==null || password==null || language==null) throw new IllegalArgumentException();
		
		//Instantiating user data 
		this.username=username;
		this.password=password;
		this.language=language;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getLanguage() {
		return this.language;
	}
	
	public synchronized boolean isOnline() {
		if(this.online==true) return true;
		return false;
	}
	
	public synchronized void setOnline(Socket control, Socket messages, DataOutputStream out) {
		setControlSocket(control);
		setMessagesSocket(messages);
		this.out=out;
		this.online=true;
	}
	
	public synchronized void setOffline() {
		setControlSocket(null);
		setMessagesSocket(null);
		this.online=false;
	}
	
	@Override
	public String toString() {
		return this.getUsername();
	}
	
	private void setControlSocket(Socket control) {
		this.control_socket=control;
	}
	
	private void setMessagesSocket(Socket messages) {
		this.messages_socket=messages;
	}
	
	public Socket getControlSocket() {
		return this.control_socket;
	}
	
	public Socket getMessagesSocket() {
		return this.messages_socket;
	}
	
	public void setRMIChannel(RMIClientInterface callback) {
		this.RMIchannel=callback;
	}
	
	public RMIClientInterface getRMIChannel() {
		return this.RMIchannel;
	}
	
	public DataOutputStream getOutputStream() {
		return this.out;
	}
}
