package communication;

/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 */
public class RequestMessage extends Message {
	@SuppressWarnings("unchecked")
	public RequestMessage(String sender) {
		super();
		message.put("SENDER", sender);		
	}
}
