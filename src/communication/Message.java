package communication;

import org.json.simple.JSONObject;
public abstract class Message {
	protected JSONObject message;
	
	/**
	 * Constructor of a message
	 * @param op operation of the message
	 */
	protected Message() {
		message= new JSONObject();
	}
	
	public Operation getOperation() {
		return Operation.valueOf((Long) this.message.get("OPERATION"));
	}
	
	/**
	 * Parse a variable number of parameters to a JSONObject
	 * @param params Variable number of strings of JSONObject. Format TYPE:CONTENT
	 */
	@SuppressWarnings("unchecked")
	public void setParameters(String ...params) {
		for (String param: params) {
			String[] components = param.split(":", 2);
			
			message.put(components[0],components[1]);
		}
	}
	
}
