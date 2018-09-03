package client;

import javax.swing.JFrame;

/**
 * GUI superclass
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;
	protected static MessageSender request_maker;
	
	public GUI(MessageSender request_maker) {
		GUI.request_maker=request_maker;
	}
	
	public GUI() {
		
	}
}
