package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;

/*import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JScrollPane;
*/

/**
 * GUI della chat con un altro utente
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class ChatGUI extends GUI{

		private static final long serialVersionUID = 7018723357317188387L;
		protected JButton btnInviaTextButton;
		protected JButton btnInviaFile;
		
		
		//public static final int WIDTH = 370;
		//public static final int HEIGHT = 400;
		
		public static final int WIDTH = 470;
		public static final int HEIGHT = 500;
		private JScrollPane scrollPane;
		private JTextArea conversationArea;
		private String title=null;
		private JScrollPane scrollPane_1;
		private JTextArea textArea;

		/**
		 * Create the frame.
		 */
		public ChatGUI(String title) {
			ChatGUI chatGUI = this;
			getContentPane().setBackground(new Color(240,240,240));
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100,WIDTH, HEIGHT);
			setResizable(false);
			this.title=title;
			setTitle("Chat con "+title);
			getContentPane().setLayout(null);
			
			//Border border = BorderFactory.createLineBorder(Color.BLACK);
			
			btnInviaTextButton = new JButton("Invia");
			btnInviaTextButton.setActionCommand("MSG_TO_FRIEND");
			btnInviaTextButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					request_maker.eventsHandler(chatGUI, e.getActionCommand());
				}
			});
			btnInviaTextButton.setBounds(306, 337, 69, 72);
			getContentPane().add(btnInviaTextButton);
			
			btnInviaFile = new JButton("Invia File");
			btnInviaFile.setActionCommand("FILE_TO_FRIEND");
			btnInviaFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					request_maker.eventsHandler(chatGUI, e.getActionCommand());
				}
			});
			/*try {
				Image img = ImageIO.read(getClass().getResource("C:\\Users\\Marco\\git\\SocialGossipCS\\src\\attach.png"));
				btnInviaFile.setIcon(new ImageIcon(img));
			} catch (Exception ex) {
					System.out.println(ex);
			}*/
			btnInviaFile.setBounds(375, 337, 79, 72);
			getContentPane().add(btnInviaFile);
			
			scrollPane = new JScrollPane();
			scrollPane.setBounds(9, 49, 445, 256);
			getContentPane().add(scrollPane);
			
			conversationArea = new JTextArea();
			conversationArea.setEditable(false);
			scrollPane.setViewportView(conversationArea);
			
			JLabel friend_field = new JLabel("");
			friend_field.setBounds(10, 15, 444, 23);
			getContentPane().add(friend_field);
			
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(6, 334, 290, 75);
			getContentPane().add(scrollPane_1);
			
			textArea = new JTextArea();
			scrollPane_1.setViewportView(textArea);
			
		}

		public JButton getBtnInviaTextButton() {
			return btnInviaTextButton;
		}

		public JButton getBtnInviaFile() {
			return btnInviaFile;
		}
		
		public void setConversationArea(String text) {
			String old = conversationArea.getText();
			conversationArea.setText(old+'\n'+text);
		}
		
		public JTextArea getConversationArea() {
			return conversationArea;
		}

		public JTextArea getTextArea() {
			return textArea;
		}
		
		public String getTitle() {
			return title;
		}
}
