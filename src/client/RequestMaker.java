package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import communication.Operation;
import communication.RequestMessage;
import communication.ResponseMessage;

public class RequestMaker {
	private Socket server_control_socket;
	//private Socket server_message_socket;
	private LoginGUI loginGUI;
	private String username;
	private String password;
	private String language;
	private DataInputStream control_in;
	private DataOutputStream control_out;

	public RequestMaker(Socket server_control_socket, Socket server_message_socket, LoginGUI loginGUI) {
		this.password="";
		this.server_control_socket=server_control_socket;
		//this.server_message_socket=server_message_socket;
		
		try {
			control_in= new DataInputStream(new BufferedInputStream(server_control_socket.getInputStream()));
			control_out= new DataOutputStream(server_control_socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Error creating Streams IN/OUT");
			e.printStackTrace();
		}
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
				System.out.println("Inviata richiesta registrazione");
				//Setting up username
				username=((RegistrationGUI) gui).getUsernameField().getText();
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting up password
				char[] pass=((RegistrationGUI) gui).getPasswordField().getPassword();
				for(char c: pass) {
					password=password+c;
				}
				System.out.println("Password: "+password);
				req.setParameters("PASSWORD:"+password);
				
				//Setting up
				language=(String) ((RegistrationGUI) gui).getComboBox().getSelectedItem();
				req.setParameters("LANGUAGE:"+language);
				System.out.println("Lingua: "+language);
				
				//Sending request
				sendRequest(req);
				
				checkResponse();
			}
			break;
			case "LOGIN":{
				System.out.println("Inviata richiesta login");
				//Setting username
				username=((LoginGUI) gui).getUsernameField().getText();
				req.setParameters("SENDER:"+username,"OPERATION:"+event);
				
				//Setting password
				password="";
				char[] pass=((LoginGUI) gui).getPasswordField().getPassword();
				for(char c: pass) password=password+c;
				req.setParameters("PASSWORD:"+password);
				
				//Sending request
				sendRequest(req);
				
				checkResponse();
			}
			break;
		}
	}
	
	public void checkResponse() {
		//Receiving request
		ResponseMessage reply=receiveResponse();
		String op= (String) reply.getParameter("OPERATION");
		switch(op) {
			case "OK":
				System.out.println("Operazione avvenuta con successo");
				break;
			case "USER_ALREADY_EXISTS":
				System.out.println("Esiste già un utente con quel nome");
				break;
			case "PERMISSION_DENIED":
				//Stampa testo body dopo
				System.out.println("Operazione non permessa");
				break;
			case "INVALID_CREDENTIALS":
				System.out.println("Credenziali errate");
				break;
			case "ERR":
				System.out.println("Errore generico");
				break;
		default:
			System.out.println("Operation "+op);
			break;
		}
	}
	
	public ResponseMessage receiveResponse() {
		String replyString= null;
		try {
			replyString= control_in.readUTF();
		} catch (IOException e) {
			System.out.println("In attesa di response");
			e.printStackTrace();
		}
		
		ResponseMessage reply=new ResponseMessage();
		reply.parseToMessage(replyString);
		return reply;
	}
	
	public void sendRequest(RequestMessage req) {
		try {
			control_out.writeUTF(req.toString());		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
