package client;

import java.io.DataOutputStream;

/**
 * The RequestMaker passes to this class only the messages containing "request" TYPE
 * Is the class that reply to the server in order to specify the port in which will receive the files.
 * It reply to the server
 * It wait for the file in the specified port
 * It communicates the arrival of a file
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class FileReceiver extends Thread {
	
	public FileReceiver(DataOutputStream out) {
		
		//Comunica ad out la nuova porta
		
		//Ascolta sulla porta
		
		
	}
}
