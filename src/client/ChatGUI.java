package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
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
		protected JTextArea textArea;
		protected JButton btnInviaTextButton;
		protected JButton btnInviaFile;
		
		
		//public static final int WIDTH = 370;
		//public static final int HEIGHT = 400;
		
		public static final int WIDTH = 470;
		public static final int HEIGHT = 500;
		private JScrollPane scrollPane;
		private JTextArea conversationArea;
		private String title=null;

		/**
		 * Create the frame.
		 */
		public ChatGUI(String title) {
			ChatGUI chatGUI = this;
			getContentPane().setBackground(Color.CYAN);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100,WIDTH, HEIGHT);
			setResizable(false);
			this.title=title;
			setTitle("Chat con "+title);
			getContentPane().setLayout(null);
			
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			
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
			/*try {
				Image img = ImageIO.read(getClass().getResource("C:\\Users\\Marco\\git\\SocialGossipCS\\src\\attach.png"));
				btnInviaFile.setIcon(new ImageIcon(img));
			} catch (Exception ex) {
					System.out.println(ex);
			}*/
			btnInviaFile.setBounds(385, 338, 69, 71);
			getContentPane().add(btnInviaFile);
			
			textArea = new JTextArea();
			textArea.setBounds(6, 337, 290, 72);
			getContentPane().add(textArea);
			textArea.setFont(new Font("Dialog", Font.PLAIN, 17));
			textArea.setBackground(new Color(240, 248, 255));
			textArea.setBorder(border);
			
			scrollPane = new JScrollPane();
			scrollPane.setBounds(9, 49, 445, 256);
			getContentPane().add(scrollPane);
			
			conversationArea = new JTextArea();
			scrollPane.setViewportView(conversationArea);
			
			JLabel friend_field = new JLabel("");
			friend_field.setBounds(10, 15, 444, 23);
			getContentPane().add(friend_field);
			
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
