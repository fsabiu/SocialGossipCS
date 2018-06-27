package client;

import javax.swing.JFrame;

public class GUI extends JFrame{
	protected RequestMaker request_maker;
	
	public GUI(RequestMaker request_maker) {
		this.request_maker=request_maker;
	}
	
	public GUI() {
		
	}
}
