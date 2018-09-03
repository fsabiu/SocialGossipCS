package communication;

/*
 * Class that represents a kind of Message (Response)
 * @author Marco Cardia
 * @author Francesco Sabiu
 */
public class ResponseMessage extends Message{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ResponseMessage() {
		super();
		j_message.put("TYPE", "response");
	}
	
}
