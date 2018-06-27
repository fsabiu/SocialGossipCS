package communication;
import java.rmi.Remote;

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
	 */
	public void registerUserRMIChannel(String nickname, RMIClientInterface callback);

	/**
	 * Dissociates user notify channel
	 * @param username
	 * @param callback
	 */
	void unregisterUserRMIChannel(String username, RMIClientInterface callback);

}
