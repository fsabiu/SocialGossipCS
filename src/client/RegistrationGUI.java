package client;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server.RequestManager;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.Font;

import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RegistrationGUI extends GUI{
		private static final long serialVersionUID = -1214889789655181279L;
		private JPanel contentPane;
		private JTextField usernameField;
		private JPasswordField passwordField;
		private JButton btnTornaALogin;
		private JButton btnInvia;
		private JComboBox<String> comboBox;
		private JFrame logInPage;
		private JLabel registrationReply;

		public RegistrationGUI(JFrame logInPage) {
			super();
			initWindowContent();
			this.logInPage=logInPage;
		}
		
		private void initWindowContent(){
			RegistrationGUI registrationGui=this;
			setVisible(true);
			this.setResizable(false);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setBounds(100, 100, 800, 600);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblPassword = new JLabel("Password");
			lblPassword.setFont(new Font("Dialog", Font.BOLD, 19));
			lblPassword.setBounds(46, 264, 165, 35);
			contentPane.add(lblPassword);
			
			JLabel lblUsername = new JLabel("Username");
			lblUsername.setFont(new Font("Dialog", Font.BOLD, 19));
			lblUsername.setBounds(46, 184, 165, 15);
			contentPane.add(lblUsername);
			
			btnInvia = new JButton("Invia");
			btnInvia.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println(e.getActionCommand());
					//Sending registration request
					request_maker.eventsHandler(registrationGui,e.getActionCommand());
				}
			});
			btnInvia.setActionCommand("REGISTER");
			btnInvia.setBounds(107, 460, 180, 50);
			contentPane.add(btnInvia);
			
			comboBox = new JComboBox<String>();
			comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"it", "en", "fr", "de", "es", "ja", "la", "pt", "ro", "ru", "sk", "sl", "sq"}));
			comboBox.setBounds(255, 396, 101, 35);
			contentPane.add(comboBox);
			
			JLabel linguaLabel = new JLabel("Seleziona Lingua");
			linguaLabel.setFont(new Font("Dialog", Font.BOLD, 19));
			linguaLabel.setBounds(45, 396, 216, 29);
			contentPane.add(linguaLabel);
			
			usernameField = new JTextField();
			usernameField.setToolTipText("Inserisci Username");
			usernameField.setForeground(new Color(150, 150, 150));
			usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
			usernameField.setColumns(10);
			usernameField.setBounds(46, 205, 310, 48);
			contentPane.add(usernameField);
			
			JLabel lblRegistratiSuSocialgossip = new JLabel("Registrati su SocialGossip");
			lblRegistratiSuSocialgossip.setHorizontalAlignment(SwingConstants.CENTER);
			lblRegistratiSuSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 27));
			lblRegistratiSuSocialgossip.setBounds(190, 22, 464, 48);
			contentPane.add(lblRegistratiSuSocialgossip);
			
			JSeparator separator = new JSeparator();
			separator.setForeground(new Color(0, 0, 0));
			separator.setOrientation(SwingConstants.VERTICAL);
			separator.setBackground(new Color(0, 0, 0));
			separator.setBounds(400, 100, 1, 460);
			contentPane.add(separator);
			
			passwordField = new JPasswordField();
			passwordField.setFont(new Font("Dialog", Font.PLAIN, 17));
			passwordField.setBounds(46, 295, 310, 48);
			contentPane.add(passwordField);
			
			JLabel lblSeiGiaRegistrato = new JLabel("Sei gia' registrato?");
			lblSeiGiaRegistrato.setFont(new Font("Dialog", Font.BOLD, 19));
			lblSeiGiaRegistrato.setBounds(495, 421, 257, 33);
			contentPane.add(lblSeiGiaRegistrato);
			
			btnTornaALogin = new JButton("Torna a Login");
			btnTornaALogin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					registrationGui.setVisible(false);
					logInPage.setVisible(true);
				}
			});
			btnTornaALogin.setActionCommand("BackToLogIn");
			btnTornaALogin.setBounds(503, 460, 180, 50);
			contentPane.add(btnTornaALogin);
			
			registrationReply = new JLabel("");
			registrationReply.setBounds(46, 92, 310, 64);
			contentPane.add(registrationReply);
		}
		
		public JComboBox<String> getComboBox() {
			return comboBox;
		}
		
		public JLabel getRegistrationReply() {
			return registrationReply;
		}
		
		public JButton getBtnInvia() {
			return btnInvia;
		}

		public JTextField getUsernameField() {
			return usernameField;
		}

		public JPasswordField getPasswordField() {
			return passwordField;
		}

		public JButton getBtnTornaALogin() {
			return btnTornaALogin;
		}
	}

