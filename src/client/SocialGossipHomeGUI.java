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
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

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
	private DefaultListModel<String> model_friend_list;
	private DefaultListModel<String> model_chatroom_list;

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
		
		model_friend_list = new DefaultListModel<String>();
		friend_list = new JList<String>(model_friend_list);
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
		btnCreaChatroom.setBounds(333, 533, 151, 35);
		contentPane.add(btnCreaChatroom);
		
		btnUniscitiAChatroom = new JButton("Unisciti a ChatRoom");
		btnUniscitiAChatroom.setActionCommand("CHAT_ADDING");
		btnUniscitiAChatroom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
		btnUniscitiAChatroom.setBounds(25, 493, 200, 35);
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
		lblChatroomAttive.setBounds(25, 111, 140, 15);
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
		new_chatroom_field.setBounds(25, 540, 279, 20);
		contentPane.add(new_chatroom_field);
		new_chatroom_field.setColumns(10);
		
		model_chatroom_list = new DefaultListModel<String>();
		chatroom_list = new JList<String>(model_chatroom_list);
		contentPane.add(chatroom_list);
		chatroom_list.addMouseListener(new MouseAdapter() {
		    @SuppressWarnings({ "rawtypes", "unused" })
			public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {
		            // Double-click detected
		            System.out.println("doppio click");
		        }
		    }
		});
		chatroom_list.setBounds(25, 137, 460, 333);
		
		JButton btnNewButton = new JButton("Chiudi ChatRoom");
		btnNewButton.setActionCommand("CHAT_CLOSING");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				request_maker.eventsHandler(socialGossipHomeGUI, e.getActionCommand());
			}
		});
		btnNewButton.setBounds(267, 493, 214, 35);
		contentPane.add(btnNewButton);
	}

	/*public DefaultListModel<User> getModelUserFriendList() {
		return modelUserFriendList;
	}

	public DefaultListModel<ChatRoom> getModelChatRoomList() {
		return modelChatRoomList;
	}*/
	
	public void setListFriends(String list) {
		/*friend_list.setModel(new AbstractListModel<String>() {
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
		});*/
		model_friend_list.removeAllElements();
		System.out.println("La lista di amici prima di replace è "+list);
		list = list.replace("[", "");
		list = list.replace("]", "");
		String[] friendList = list.split(", ");
		for (String friend : friendList) {
			model_friend_list.addElement(friend);
		}
	}
	
	public void addFriendToList(String user) {
		model_friend_list.addElement(user);
	}
	
	public String getSelectedListFriend() {
		String friend=friend_list.getSelectedValue();
		System.out.println("L'amico selezionato è "+friend_list.getSelectedValue());
		return friend;
	}
	
	public void setChatroomList(String belongsList, String notBelongsList) {
		/*chatroom_list.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			
			String[] first = belongsList.split(", ");
			String[] second = notBelongsList.split(", ");
			String[] values = combine(first,second);
			
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				values[index] = values[index].replace("[", "");
				values[index] = values[index].replace("]", "");
				return values[index];
			}
		});*/
		model_chatroom_list.removeAllElements();
		belongsList = belongsList.replace("[", "").replace("]", "");
		String[] chatroomsList = belongsList.split(", ");
		for (String chatroom : chatroomsList) {
			model_chatroom_list.addElement(chatroom);
		}
		notBelongsList = notBelongsList.replace("[", "").replace("]", "");
		chatroomsList = notBelongsList.split(", ");
		for (String chatroom : chatroomsList) {
			model_chatroom_list.addElement(chatroom);
		}
	}
	
	public void addChatroom(String chatroom) {
		model_chatroom_list.addElement(chatroom);
	}
	
	public void removeChatroom(String chatroom) {
		model_chatroom_list.removeElement(chatroom);
	}
	
	public static String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
	
	public String getSelectedListChatroom() {
		String chatroom=chatroom_list.getSelectedValue();
		System.out.println("La chatroom selezionata è "+chatroom_list.getSelectedValue());
		return chatroom;
	}
	
	public void logoutGUI() {
		setVisible(false);
		loginGUI=null;
		//loginGUI.setVisible(true);
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
}
