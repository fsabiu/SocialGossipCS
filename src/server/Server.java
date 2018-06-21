package server;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author March Cardia
 * @author Francesco Sabiu
 * It represents the class (singleton) of the server
 */
public class Server implements Runnable{

	private Graph<User> network;
	private ConcurrentHashMap<String, User> usersbyname;
	private ConcurrentHashMap<String, Chatroom> chatrooms; //Hasmap of existing chatrooms (Map of <Name, Chatroom>)
	
	//private ServerSocket listenerSocket = null; //socket in cui e' in ascolto il server
	//private ThreadPoolExecutor executor = null; //pool di thread per gestire i vari client che arrivano
	
	
	/**
	 * Private constructor for the Singleton Server
	 * @param port
	 */
	private void Server(int port) {
		network= new Graph<User>();
		usersbyname= new ConcurrentHashMap<String, User>();
	}
	
	@Override
	public void run() {
		
	}
	
	/**
	 * It creates the instance of the server through its constructor
	 */
	public static void main() {
		try {
			//Creating instance of the Server
			Server CSServer= new Server(5500);
			
			//Running the server
			CSServer.run();
		}
		catch {
			
		}
	}
}
