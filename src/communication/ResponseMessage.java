package communication;

public class ResponseMessage extends Message{
	public ResponseMessage() {
		super();
		message.put("TYPE", "response");
	}
	
}
