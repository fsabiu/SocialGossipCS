package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import server.thread.UserRequestHandler;


/**
 * @author March Cardia
 * @author Francesco Sabiu
 * It represents the class (singleton) of the server
 */
public class Server implements Runnable{

	private Graph<User> network;
	private ConcurrentHashMap<String, User> usersbyname;
	private ConcurrentHashMap<String, Chatroom> chatrooms; //Hasmap of existing chatrooms (Map of <Name, Chatroom>)
	
	private ServerSocket listenerSocket = null; //Server listener socket
	private ThreadPoolExecutor performer = null; //Manager thread pool performer
	
	
	/**
	 * Private constructor for the Singleton Server
	 * @param port
	 * @throws IOException 
	 */
	public Server(int port) throws IOException {
		network= new Graph<User>();
		usersbyname= new ConcurrentHashMap<String, User>();
		chatrooms= new ConcurrentHashMap<String, Chatroom>();
		performer=(ThreadPoolExecutor) Executors.newCachedThreadPool();
		listenerSocket = new ServerSocket(port);
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				//The server is ready to listen new requests
				Socket Client = listenerSocket.accept();
				
				//Submitting client requests to the thread pool
				performer.submit(new RequestManager(Client,network,chatrooms, usersbyname));
				
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
