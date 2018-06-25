package communication;

public class ResponseMessage extends Message{
	public ResponseMessage() {
		super();
		j_message.put("TYPE", "response");
	}
	
}
