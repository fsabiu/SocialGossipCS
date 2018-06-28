package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import javax.swing.AbstractListModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SocialGossipHomeGUI extends GUI{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame loginGUI;
	private JPanel contentPane;
	private JLabel WelcomeText;
	private JTextField textField;
	private JButton btnLogout;
	private JButton btnAvviaChat;
	private JButton btnCerca;
	private JButton btnUniscitiAChatroom;
	private JButton btnCreaChatroom;
	private JTextField textField_1;
	private JList<String> friend_list;
	private JList<String> chatroom_list;
	//private JList<User> userFriendList;
	//private DefaultListModel<User> modelUserFriendList = new DefaultListModel<User>();
	//private JList<ChatRoom> chatRoomList;
	//private DefaultListModel<ChatRoom> modelChatRoomList = new DefaultListModel<ChatRoom>();

	/**
	 * Create the frame.
	 */
	public SocialGossipHomeGUI(JFrame loginGUI) {
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		SocialGossipHomeGUI socialGossipHomeGUI=this;
		this.loginGUI=loginGUI;
		setTitle("Social Gossip");
		setResizable(false);
		setVisible(true);
		setBounds(100, 100, 800,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		WelcomeText = new JLabel("");
		WelcomeText.setBounds(12, 535, 290, 28);
		contentPane.add(WelcomeText);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(550, 70, 200, 400);
		contentPane.add(scrollPane);
		
		friend_list = new JList<String>();
		friend_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"Prova uno", "Prova due"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane.setViewportView(friend_list);
		
		/*userFriendList = new JList<User>();
		userFriendList.setModel(modelUserFriendList);
		scrollPane.setViewportView(userFriendList);*/
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 122, 440, 367);
		contentPane.add(scrollPane_1);
		
		chatroom_list = new JList<String>();
		chatroom_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"chatroom 1", "chatroom 2"};
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		scrollPane_1.setViewportView(chatroom_list);
		
		/*chatRoomList = new JList<ChatRoom>();
		chatRoomList.setModel(modelChatRoomList);
		scrollPane_1.setViewportView(chatRoomList);*/
		
		btnCreaChatroom = new JButton("Crea ChatRoom");
		btnCreaChatroom.setBounds(12, 499, 151, 35);
		contentPane.add(btnCreaChatroom);
		
		btnUniscitiAChatroom = new JButton("Unisciti a ChatRoom");
		btnUniscitiAChatroom.setBounds(175, 499, 200, 35);
		contentPane.add(btnUniscitiAChatroom);
		
		JLabel lblCercaUtente = new JLabel("Cerca Utente:");
		lblCercaUtente.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCercaUtente.setBounds(12, 18, 110, 15);
		contentPane.add(lblCercaUtente);
		
		textField = new JTextField();
		textField.setBounds(149, 11, 207, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnCerca = new JButton("Cerca");
		btnCerca.setBounds(364, 13, 88, 25);
		contentPane.add(btnCerca);
		
		JLabel lblChatroomAttive = new JLabel("ChatRoom Attive:");
		lblChatroomAttive.setFont(new Font("Dialog", Font.BOLD, 14));
		lblChatroomAttive.setBounds(15, 106, 140, 15);
		contentPane.add(lblChatroomAttive);
		
		btnLogout = new JButton("Logout");
		btnLogout.setActionCommand("LOGOUT");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
		btnLogout.setBounds(671, 538, 117, 25);
		contentPane.add(btnLogout);
		
		JLabel lblAmiciOnline = new JLabel("I Tuoi Amici:");
		lblAmiciOnline.setFont(new Font("Dialog", Font.BOLD, 14));
		lblAmiciOnline.setBounds(550, 55, 110, 15);
		contentPane.add(lblAmiciOnline);
		
		btnAvviaChat = new JButton("Avvia Chat");
		btnAvviaChat.setBounds(550, 478, 200, 35);
		contentPane.add(btnAvviaChat);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(505, 0, 2, 600);
		contentPane.add(separator);
		
		JLabel lblAggiungiAmico = new JLabel("Aggiungi amico:");
		lblAggiungiAmico.setFont(new Font("Dialog", Font.BOLD, 14));
		lblAggiungiAmico.setBounds(12, 47, 127, 30);
		contentPane.add(lblAggiungiAmico);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(149, 51, 207, 30);
		contentPane.add(textField_1);
		
		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.setBounds(364, 53, 88, 25);
		contentPane.add(btnAggiungi);
	}

	/*public DefaultListModel<User> getModelUserFriendList() {
		return modelUserFriendList;
	}

	public DefaultListModel<ChatRoom> getModelChatRoomList() {
		return modelChatRoomList;
	}*/
	
	public void logoutGUI() {
		setVisible(false);
		loginGUI.setVisible(true);
	}
	
	public JPanel getContentPane() {
		return contentPane;
	}

	public JTextField getTextField() {
		return textField;
	}

	public JButton getBtnLogout() {
		return btnLogout;
	}

	public JButton getBtnAvviaChat() {
		return btnAvviaChat;
	}

	public JButton getBtnCerca() {
		return btnCerca;
	}

	public JButton getBtnUniscitiAChatroom() {
		return btnUniscitiAChatroom;
	}

	public JButton getBtnCreaChatroom() {
		return btnCreaChatroom;
	}

	/*public JList<User> getUserFriendList() {
		return userFriendList;
	}

	public JList<ChatRoom> getChatRoomList() {
		return chatRoomList;
	}*/

	public JLabel getWelcomeText() {
		return WelcomeText;
	}

	public void setWelcomeText(String welcomeText) {
		WelcomeText.setText(welcomeText);
	}
}
