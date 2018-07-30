package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

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
		protected JTextArea conversationArea;
		protected JTextArea textArea;
		protected JButton btnInviaTextButton;
		protected JButton btnInviaFile;
		
		
		//public static final int WIDTH = 370;
		//public static final int HEIGHT = 400;
		
		public static final int WIDTH = 470;
		public static final int HEIGHT = 500;
		protected JScrollPane scrollPane;
		protected JScrollPane scrollPane_1;

		/**
		 * Create the frame.
		 */
		public ChatGUI(String title) {
			getContentPane().setBackground(Color.CYAN);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100,WIDTH, HEIGHT);
			setResizable(false);
			setTitle(title);
			getContentPane().setLayout(null);
			
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			
			scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 12, 350, 250);
			getContentPane().add(scrollPane);

			conversationArea = new JTextArea();
			scrollPane.setViewportView(conversationArea);
			conversationArea.setFont(new Font("Dialog", Font.BOLD, 15));
			conversationArea.setBackground(new Color(240, 248, 255));
			conversationArea.setEditable(false);
			conversationArea.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
			
			scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(10, 316, 290, 72);
			getContentPane().add(scrollPane_1);
			
			btnInviaTextButton = new JButton("Invia");
			btnInviaTextButton.setBounds(306, 316, 69, 72);
			getContentPane().add(btnInviaTextButton);
			
			btnInviaFile = new JButton("Invia File");
			/*try {
				Image img = ImageIO.read(getClass().getResource("C:\\Users\\Marco\\git\\SocialGossipCS\\src\\attach.png"));
				btnInviaFile.setIcon(new ImageIcon(img));
			} catch (Exception ex) {
					System.out.println(ex);
			}*/
			btnInviaFile.setBounds(385, 317, 69, 71);
			getContentPane().add(btnInviaFile);
			
			textArea = new JTextArea();
			textArea.setBounds(10, 316, 290, 72);
			getContentPane().add(textArea);
			textArea.setFont(new Font("Dialog", Font.PLAIN, 17));
			textArea.setBackground(new Color(240, 248, 255));
			textArea.setBorder(border);
			
		}

		public JButton getBtnInviaTextButton() {
			return btnInviaTextButton;
		}

		public JButton getBtnInviaFile() {
			return btnInviaFile;
		}

		public JTextArea getConversationArea() {
			return conversationArea;
		}

		public JTextArea getTextArea() {
			return textArea;
		}

}
