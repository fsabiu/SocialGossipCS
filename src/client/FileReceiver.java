package client;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import util.Config;

public class FileReceiver extends Thread{

	private String hostname;
	private int port;
	private ChatGUI chatGUI;
	private String sender;
	private String filename;

	public FileReceiver(String hostname, int port, GUI gui, String sender, String filename) {
		this.hostname = hostname;
		this.port = port;
		this.chatGUI = (ChatGUI) gui;
		this.sender = sender;
		this.filename = filename;
	}
	
	public void run() {
		ServerSocketChannel socketChannel = null;
		SocketChannel sender = null;
		FileChannel file = null;
		
		try {
			socketChannel = ServerSocketChannel.open();
			socketChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Path path = Paths.get(new File("").getAbsolutePath()+Config.DOWNLOAD_DIRECTORY+filename);
		
		//Creo il file che andreamo a ricevere
		try {
			file = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			sender = socketChannel.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			while (sender.read(buffer) > 0) {
			    buffer.flip();
			    
			    //scrivo su file
			    while (buffer.hasRemaining()) {
			    	file.write(buffer);
			    }
			    
			    buffer.clear();
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Ricevuto il file");
		chatGUI.setConversationArea("L'utente "+sender+" ti ha inviato il file "+file.toString());
	}

}
