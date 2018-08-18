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

import org.json.simple.JSONArray;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.AbstractListModel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;

public class SocialGossipHomeGUI extends GUI{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFrame loginGUI;
	private JPanel contentPane;
	private JTextField user_search_field;
	private JButton btnLogout;
	private JButton btnAvviaChat;
	private JButton btnCerca;
	private JButton btnUniscitiAChatroom;
	private JButton btnCreaChatroom;
	private JTextField user_to_add_field;
	private JList<String> friend_list;
	private JList<String> chatroom_list;
	private JTextField new_chatroom_field;
	//private JList<User> userFriendList;
	//private DefaultListModel<User> modelUserFriendList = new DefaultListModel<User>();
	//private JList<ChatRoom> chatRoomList;
	//private DefaultListModel<ChatRoom> modelChatRoomList = new DefaultListModel<ChatRoom>();

	/**
	 * Create the frame.
	 */
	public SocialGossipHomeGUI(JFrame loginGUI, String username) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SocialGossipHomeGUI socialGossipHomeGUI=this;
		this.loginGUI=loginGUI;
		setTitle("Social Gossip");
		setResizable(false);
		//setVisible(true);
		setBounds(100, 100, 800,600);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(550, 70, 200, 400);
		contentPane.add(scrollPane);
		
		friend_list = new JList<String>();
		scrollPane.setViewportView(friend_list);
		
		/*chatRoomList = new JList<ChatRoom>();
		chatRoomList.setModel(modelChatRoomList);
		scrollPane_1.setViewportView(chatRoomList);*/
		
		btnCreaChatroom = new JButton("Crea ChatRoom");
		btnCreaChatroom.setActionCommand("CHAT_CREATION");
		btnCreaChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
		btnCreaChatroom.setBounds(301, 535, 151, 35);
		contentPane.add(btnCreaChatroom);
		
		btnUniscitiAChatroom = new JButton("Unisciti a ChatRoom");
		btnUniscitiAChatroom.setBounds(10, 500, 200, 35);
		contentPane.add(btnUniscitiAChatroom);
		
		JLabel lblCercaUtente = new JLabel("Cerca Utente:");
		lblCercaUtente.setFont(new Font("Dialog", Font.BOLD, 14));
		lblCercaUtente.setBounds(12, 18, 110, 15);
		contentPane.add(lblCercaUtente);
		
		user_search_field = new JTextField();
		user_search_field.setBounds(149, 11, 207, 30);
		contentPane.add(user_search_field);
		user_search_field.setColumns(10);
		
		btnCerca = new JButton("Cerca");
		btnCerca.setActionCommand("LOOKUP");
		btnCerca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
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
		btnAvviaChat.setActionCommand("STARTCHAT");
		btnAvviaChat.setBounds(550, 478, 200, 35);
		btnAvviaChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Button avvia chat pressed");
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
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
		
		user_to_add_field = new JTextField();
		user_to_add_field.setColumns(10);
		user_to_add_field.setBounds(149, 51, 207, 30);
		contentPane.add(user_to_add_field);
		
		JButton btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.setActionCommand("FRIENDSHIP");
		btnAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
		btnAggiungi.setBounds(364, 53, 88, 25);
		contentPane.add(btnAggiungi);
		
		JLabel profileName = new JLabel("Profilo di "+username);
		profileName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		profileName.setBounds(550, 11, 200, 35);
		contentPane.add(profileName);
		
		new_chatroom_field = new JTextField();
		new_chatroom_field.setBounds(12, 543, 279, 20);
		contentPane.add(new_chatroom_field);
		new_chatroom_field.setColumns(10);
		
		JList<String> chatroom_list = new JList<String>();
		chatroom_list.setBounds(25, 132, 427, 327);
		//contentPane.add(chatroom_list);
	}

	/*public DefaultListModel<User> getModelUserFriendList() {
		return modelUserFriendList;
	}

	public DefaultListModel<ChatRoom> getModelChatRoomList() {
		return modelChatRoomList;
	}*/
	
	public void setListFriends(String arrayList) {
		friend_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			
			String[] values = arrayList.split(", ");
			
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				values[index] = values[index].replace("[", "");
				values[index] = values[index].replace("]", "");
				return values[index];
			}
		});
	}
	
	public void setChatroomList(String belongsList, String notBelongsList) {
		chatroom_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			
			String[] values = belongsList.split(", ");
			
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				values[index] = values[index].replace("[", "");
				values[index] = values[index].replace("]", "");
				return values[index];
			}
		});
	}
	
	public String getSelectedListFriend() {
		String friend=friend_list.getSelectedValue();
		System.out.println("L'elemento selezionato è"+friend_list.getSelectedValue());
		return friend;
	}
	
	public void logoutGUI() {
		setVisible(false);
		loginGUI.setVisible(true);
	}
	
	public JPanel getContentPane() {
		return contentPane;
	}

	public JTextField getUserSearchField() {
		return user_search_field;
	}
	
	public JTextField getUserToAddField() {
		return user_to_add_field;
	}
	
	public JTextField getNewChatroomField() {
		return new_chatroom_field;
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

	/*public JLabel getWelcomeText() {
		return lblNewChatroom;
	}

	public void setWelcomeText(String welcomeText) {
		lblNewChatroom.setText(welcomeText);
	}*/
	
	public void createChatGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatGUI frame = new ChatGUI("ciao");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
