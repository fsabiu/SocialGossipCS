package client;

public class MessageListener extends Thread{

	private MessageSender message_sender;

	public MessageListener(MessageSender message_sender) {
		this.message_sender=message_sender;
	}

	public void run() {
	}
}
