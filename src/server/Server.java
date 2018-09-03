package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import util.Config;
import util.Graph;


/**
 * @author March Cardia
 * @author Francesco Sabiu
 * It represents the class (singleton) of the server
 */
public class Server implements Runnable{

	private Graph<User> network;
	private ConcurrentHashMap<String, User> usersbyname; //Hasmap of existing users (Map of <username, User>)
	private ConcurrentHashMap<String, Chatroom> chatrooms; //Hasmap of existing chatrooms (Map of <Name, Chatroom>)
	
	private ServerSocket listenerSocket = null; //Server listener socket
	private ThreadPoolExecutor performer = null; //Manager thread pool performer
	
	
	/**
	 * Private constructor for the Singleton Server
	 * @param port
	 * @throws IOException 
	 */
	public Server(int port) {
		network= new Graph<User>();
		usersbyname= new ConcurrentHashMap<String, User>();
		chatrooms= new ConcurrentHashMap<String, Chatroom>();
		performer=(ThreadPoolExecutor) Executors.newCachedThreadPool();
		try {
			listenerSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Another server instance is running. Stop it and try again.");
			System.exit(-3);
		}
	}
	
	
	@Override
	public void run() {
		try {
			//Initializating RMI
			startRMI();
			
			while(true) {
				System.out.println("Waiting for connections...");
				//The server is ready to listen new requests
				Socket Client = listenerSocket.accept();
				
				//Submitting client requests to the thread pool
				performer.submit(new RequestManager(Client,network,chatrooms, usersbyname));
				
			}
		} 
		catch (IOException e)  {
			System.out.println("Error while instantiating connection with client");;
		}
	}
	
	/**
	 * It creates the instance of the server through its constructor
	 */
	public static void main(String[] args) {
		//Creating instance of the Server
		Server CSServer= new Server(Config.SERVER_TCP_PORT);
		
		//Running the server
		CSServer.run();
	}
	
	/**
	 * RMI initialization routine
	 * @throws RemoteException
	 */
	private void startRMI() throws RemoteException {
		//Instantiating RMI manager
		RMIChannelManager RMIUserChannelManager = new RMIChannelManager(usersbyname);
		
		//Registry creation
		LocateRegistry.createRegistry(Config.SERVER_RMI_PORT);
		
		//Getting registry
		Registry reg = LocateRegistry.getRegistry(Config.SERVER_RMI_PORT);
		
		//Instantiating stub
		reg.rebind(Config.SERVER_RMI_SERVICE_NAME,RMIUserChannelManager);
	}
	
}
