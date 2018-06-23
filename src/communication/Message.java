package communication;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Message {
	protected JSONObject message;
	
	/**
	 * Constructor of a message
	 * @param op operation of the message
	 */
	protected Message(Operation op) {
		message= new JSONObject();
		try {
			message.put("OPERATION", new Long(op.getValue()));
		} catch (JSONException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Operation getOperation() {
		try {
			return Operation.valueOf((Long) this.message.get("OPERATION"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Parse a variable number of parameters to a JSONObject
	 * @param params Variable number of strings of JSONObject. Format TYPE:CONTENT
	 */
	public void setParameters(String ...params) {
		for (String param: params) {
			String[] components = param.split(":", 2);
			try {
				message.put(components[0],components[1]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String
	
}
