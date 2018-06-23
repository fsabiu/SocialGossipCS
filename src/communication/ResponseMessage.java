package communication;

public class ResponseMessage extends Message{
	@SuppressWarnings("unchecked")
	public ResponseMessage(Operation op) {
		super();
		message.put("OPERATION", op);
	}
	
}
