package communication;


/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 */
public class RequestMessage extends Message {
	@SuppressWarnings("unchecked")
	public RequestMessage(Operation op, String sender) {
		super();
		message.put("OPERATION", op);
		message.put("SENDER", sender);		
	}
	
}
