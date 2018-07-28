package communication;

public class ResponseMessage extends Message{
	@SuppressWarnings("unchecked")
	public ResponseMessage() {
		super();
		j_message.put("TYPE", "response");
		j_message.put("RESPONSE_TYPE", "");
	}
	
}
