package server;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIClientInterface;
import communication.RMIServerInterface;

public class RMIChannelManager extends UnicastRemoteObject implements RMIServerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String, User> usersbyname;
	
	public RMIChannelManager(ConcurrentHashMap<String, User> usersbyname) throws RemoteException {
		this.usersbyname=usersbyname;
	}

	@Override
	public void registerUserRMIChannel(String username, RMIClientInterface callback) throws RemoteException {
		//Getting user
		User u= usersbyname.get(username);
		System.out.println("Aggiungendo rmi");
		u.setRMIChannel(callback);
		System.out.println("Aggiunto");
	}

	@Override
	public void unregisterUserRMIChannel(String username, RMIClientInterface callback) throws RemoteException {
		//Getting user
		User u= usersbyname.get(username);
		u.setRMIChannel(null);
	}

}
