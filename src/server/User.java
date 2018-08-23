package server;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import communication.RMIClientInterface;


/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 * The class represent a subscribed user, identified by unique username.
 */
public class User {
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
	private transient Socket message_socket=null;
	private ObjectOutputStream control_out=null;
	private ObjectOutputStream message_out=null;
	private ObjectInputStream control_in=null;
	private ObjectInputStream message_in=null;
	
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
	
	public synchronized void setOnline(Socket control, Socket messages, ObjectOutputStream control_out, ObjectOutputStream message_out, ObjectInputStream control_in, ObjectInputStream message_in) throws IOException {
		setControlSocket(control);
		setMessageSocket(messages);
		this.control_out=control_out;
		this.message_out=message_out;
		this.control_in=control_in;
		this.message_in=message_in;
		this.online=true;
	}
	
	public synchronized void setOffline() {
		setControlSocket(null);
		setMessageSocket(null);
		this.online=false;
	}
	
	@Override
	public String toString() {
		return this.getUsername();
	}
	
	private void setControlSocket(Socket control) {
		this.control_socket=control;
	}
	
	private void setMessageSocket(Socket message) {
		this.message_socket=message;
	}
	
	public Socket getControlSocket() {
		return this.control_socket;
	}
	
	public Socket getMessageSocket() {
		return this.message_socket;
	}
	
	public void setRMIChannel(RMIClientInterface callback) {
		this.RMIchannel=callback;
	}
	
	public RMIClientInterface getRMIChannel() {
		return this.RMIchannel;
	}
	
	public ObjectOutputStream getControlOutputStream() {
		return this.control_out;
	}
	
	public ObjectOutputStream getMessageOutputStream() {
		return this.message_out;
	}
	
	public ObjectInputStream getMessageInputStream() {
		return this.message_in;
	}
	
	public ObjectInputStream getControlInputStream() {
		return this.control_in;
	}
}
