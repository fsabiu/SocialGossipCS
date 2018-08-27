package communication;

/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 */
public class RequestMessage extends Message {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public RequestMessage(String sender) {
		super();
		j_message.put("TYPE", "request");
		j_message.put("SENDER", sender);		
	}
	
	@SuppressWarnings("unchecked")
	public RequestMessage() {
		super();
		j_message.put("TYPE", "request");
	}
}
