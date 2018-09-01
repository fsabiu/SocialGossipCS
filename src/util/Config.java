package util;

public class Config 
{
	//TCP constants
	public static String SERVER_HOST_NAME = "192.168.43.181";
	public static final int	SERVER_TCP_PORT= 5000;
	
	//RMI constants
	public static final String SERVER_RMI_SERVICE_NAME = "SocialGossipCSNotifier";
	public static final int SERVER_RMI_PORT = 6000;

	//MULTICAST constants
	public static final String FIRST_MULTICAST_ADDR = "224.0.0.1";
	public static final String LAST_MULTICAST_ADDR = "224.0.0.255";
	
	//FILE SYSTEM constants
	public static final String DOWNLOAD_DIRECTORY = "/resources/downloads/";
	public static final String UPLOAD_DIRECTORY = "/resources/upload/";
	
	//Translation parameter
	//public static final String TRANSLATOR_URL = "http://api.mymemory.translated.net/get?";
	public static final String TRANSLATOR_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20180823T142704Z.9bbaa03155b76683.dfa9189bd9b7037b69343d1900653912c8cd4c9e&";
}