package client;

import javax.swing.JFrame;

public class GUI extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static RequestMaker request_maker;
	
	public GUI(RequestMaker request_maker) {
		GUI.request_maker=request_maker;
	}
	
	public GUI() {
		
	}
}
