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

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import util.Config;

/**
 * Class used to receive files
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
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
		SocketChannel sender_sock = null;
		FileChannel file = null;
		
		try {
			socketChannel = ServerSocketChannel.open();
			socketChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e) {
			System.out.println("Error while creating socket");
		}
		Path path = Paths.get(new File("").getAbsolutePath()+Config.DOWNLOAD_DIRECTORY+filename);
		
		// Creating file to be received
		try {
			file = FileChannel.open(path, EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));
		} catch (IOException e) {
			System.out.println("Error while creating file to receive");
		}
		
		try {
			sender_sock = socketChannel.accept();
		} catch (IOException e) {
			System.out.println("Error while accepting connection.");
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		try {
			while (sender_sock.read(buffer) > 0) {
			    buffer.flip();
			    
			    //Writing into the file
			    while (buffer.hasRemaining()) {
			    	file.write(buffer);
			    }
			    
			    buffer.clear();
			}
			file.close();
		} catch (IOException e) {
			System.out.println("Error while copying file");
		}
		
		//Showing message to GUI
		JOptionPane pane = new JOptionPane(sender+" ti ha inviato "+filename);
        JDialog dialog = pane.createDialog(null, "Notification");
        dialog.setModal(false);
        dialog.setVisible(true);
		chatGUI.setConversationArea(sender+" ti ha inviato "+filename);
	}

}
