package util;

public class Config 
{
	//TCP constants
	public static final String SERVER_HOST_NAME = "10.0.0.207";
	public static final int	SERVER_TCP_PORT= 5000;
	
	//RMI constants
	public static final String SERVER_RMI_SERVICE_NAME = "SocialGossipCSNotifier";
	public static final int SERVER_RMI_PORT = 6000;

	//MULTICAST constants
	public static final String FIRST_MULTICAST_ADDR = "224.0.0.1";
	public static final String LAST_MULTICAST_ADDR = "224.0.0.255";
	
	//FILE SYSTEM constants
	public static final String DOWNLOAD_DIRECTORY = "/resources/downloads/";
	
	//Translation parameter
	public static final String TRANSLATOR_URL = "http://api.mymemory.translated.net/get?";
}