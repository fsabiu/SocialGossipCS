package communication;

import org.json.JSONException;

/**
 * 
 * @author Marco Cardia
 * @author Francesco Sabiu
 */
public class RequestMessage extends Message {
	public RequestMessage(Operation op, String sender) {
		super(op);
		try {
			message.put("SENDER", sender);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
