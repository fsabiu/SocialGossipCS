package communication;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * 
 * @author Francesco Sabiu
 * @author Marco Cardia
 *
 */
public interface RMIServerInterface extends Remote {

	/**
	 * Associates a notify channel to a user, in order to notify it.
	 * @param nickname
	 * @param callback
	 * @throws RemoteException 
	 */
	public void registerUserRMIChannel(String nickname, RMIClientInterface callback) throws RemoteException;

	/**
	 * Dissociates user notify channel
	 * @param username
	 * @param callback
	 * @throws RemoteException 
	 */
	void unregisterUserRMIChannel(String username, RMIClientInterface callback) throws RemoteException;

}
