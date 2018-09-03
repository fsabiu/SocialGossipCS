package communication;

public class ResponseMessage extends Message{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public ResponseMessage() {
		super();
		j_message.put("TYPE", "response");
	}
	
}
