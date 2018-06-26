package server;

import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import communication.RMIClientInterface;
import communication.RMIServerInterface;

public class RMIChannelManager extends RemoteObject implements RMIServerInterface /* implements RMIServerInterface */ {

	private ConcurrentHashMap<String, User> usersbyname;
	
	public RMIChannelManager(ConcurrentHashMap<String, User> usersbyname) {
		this.usersbyname=usersbyname;
	}

	@Override
	public void registerUserRMIChannel(String username, RMIClientInterface callback) {
		//Getting user
		User u= usersbyname.get(username);
		u.setRMIChannel(callback);
	}

	@Override
	public void unregisterUserRMIChannel(String username, RMIClientInterface callback) {
		//Getting user
		User u= usersbyname.get(username);
		u.setRMIChannel(null);
	}

}
