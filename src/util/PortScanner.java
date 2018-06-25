package util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class PortScanner {
	public static final int MIN_PORT_NUMBER = 1024;
	public static final int MAX_PORT_NUMBER = 65535;
	
	/**
	 * 
	 * @return a free port if available, -1 otherwise
	 */
	public synchronized static int freePort() {
		try {
	        for(int i = MIN_PORT_NUMBER;  i <= MAX_PORT_NUMBER; i++) {
	            if(available(i))
	                return i;
	        }
		} catch(IllegalArgumentException e) {
			return -1;
		}
		return -1;
}
	
	/**
	 * 
	 * @param port
	 * @return true if given port is free, false otherwise
	 */
	public synchronized static boolean available(int port) {
	    if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try  {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	        
	    } catch (IOException e) {
	    } 
	    finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
		            }
		        }
		    }
		    return false;
		}

	}
