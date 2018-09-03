package server;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIClientInterface;
import communication.RMIServerInterface;

/**
 * Class that manages users RMI callback
 * @author Francesco Sabiu
 * @author Marco Cardia
 *
 */
public class RMIChannelManager extends UnicastRemoteObject implements RMIServerInterface {
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String, User> usersbyname;
	
	public RMIChannelManager(ConcurrentHashMap<String, User> usersbyname) throws RemoteException {
		this.usersbyname=usersbyname;
	}

	@Override
	public void registerUserRMIChannel(String username, RMIClientInterface callback) throws RemoteException {
		//Getting user
		User u= usersbyname.get(username);
		u.setRMIChannel(callback);
	}

	@Override
	public void unregisterUserRMIChannel(String username, RMIClientInterface callback) throws RemoteException {
		//Getting user
		User u= usersbyname.get(username);
		u.setRMIChannel(null);
	}

}
