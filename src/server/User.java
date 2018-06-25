package server;
import java.io.Serializable;
import java.net.Socket;



/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 * The class represent a subscribed user, identified by unique username.
 */
public class User implements Serializable {
	//Personal data
	private String username;
	private String password;
	private String language;
	private boolean online;
	
	//Managing connections
	private transient Socket notificationChannel=null;
	//RMI Channel
	private RMIClientNotifyEvent RMIchannel = null;
	
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
	
	public synchronized void setOnline(Socket client) {
		setNotificationChannel(client);
		this.online=true;
	}
	
	public synchronized void setOffline() {
		setNotificationChannel(null);
		this.online=false;
	}
	
	@Override
	public String toString() {
		return this.getUsername();
	}
	
	private void setNotificationChannel(Socket sock_address) {
		this.notificationChannel=sock_address;
	}
	
	public Socket getNotificationChannel() {
		return this.notificationChannel;
	}
}
