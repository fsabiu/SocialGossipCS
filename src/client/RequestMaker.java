package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import communication.RequestMessage;

public class RequestMaker {
	private Socket server_control_socket;
	//private Socket server_message_socket;
	private LoginGUI loginGUI;
	private String username;
	private String password;
	private String language;

	public RequestMaker(Socket server_control_socket, Socket server_message_socket, LoginGUI loginGUI) {
		this.server_control_socket=server_control_socket;
		//this.server_message_socket=server_message_socket;
		this.loginGUI=loginGUI;		
	}
	
	//public void run() {
		
		//Pressing login button
		/*loginGUI.getBtnLogin().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUsername();
				setPassword();
				sendRequest();
			}
		});*/
	
		//Pressing SigIn button
		/*loginGUI.getBtnSignIn().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//NECESSARIO INVOCARE METODI ADD ACTION LISTENER PER OGNI BOTTONE
			}
		});*/
	//}
	
	/* HO CREATO UNA SUPERCLASSE GUI CHE HA COME ATTRIBUTO UN REQUESTMAKER, OVVERO
	 * UN GESTORE DI EVENTI. IN GENERALE, ALL'ATTIVAZIONE DI UN EVENTO, SI CHIAMA IL METODO
	 * EVENTS HANDLER, CHE GESTISCE L'EVENTO IMPOSTANDO I CAMPI DEL MESSAGGIO E INVIANDO LA 
	 * RICHIESTA RELATIVA.
	 * CIASCUNA INTERFACCIA GRAFICA POTRA' CHIAMARE IL METODO IN QUANTO L'OGGETTO REQUESTMAKER E' UN
	 * ATTRIBUTO PROTECTED
	 * COSA FARA IL METODO RUN? REQUEST MAKER POTREBBE NON ESSERE PIU UNA CLASSE CHE ESTENDE THREADS
	 * (L'INTERFACCIA PERO SI BLOCCA!!!)
	 * ALTRA SOLUZIONE: PER OGNI EVENTO CHIAMA RUN! 
	 */
	public void eventsHandler(GUI gui, String event) {
		RequestMessage req = new RequestMessage(username);
		switch(event) {
			case "REGISTER":{
				//Setting up username
				username=((RegistrationGUI) gui).getUsernameField().getText();
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting up password
				char[] pass=((RegistrationGUI) gui).getPasswordField().getPassword();
				for(char c: pass) password=password+c;
				req.setParameters("PASSWORD"+password);
				
				//Setting up
				language=((RegistrationGUI) gui).getComboBox().getToolTipText();
				req.setParameters("LANGUAGE:"+language);
				
				//Sending request
				sendRequest(req);
			}
			break;
			case "LOGIN":{
				//Setting username
				username=((LoginGUI) gui).getUsernameField().getText();
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting password
				char[] pass=((LoginGUI) gui).getPasswordField().getPassword();
				for(char c: pass) password=password+c;
				req.setParameters("PASSWORD:"+password);
				
				//Sending request
				sendRequest(req);
			}
			break;
		}
	}
	
	
	
	public void sendRequest(RequestMessage req) {
		try {
			DataOutputStream control_out= new DataOutputStream(server_control_socket.getOutputStream());
			control_out.writeUTF(req.toString());		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
