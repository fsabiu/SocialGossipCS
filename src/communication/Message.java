package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class that represents a message
 * @author Marco Cardia
 * @author Francesco Sabiu
 *
 */
public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	protected JSONObject j_message;
	
	/**
	 * Constructor of a message
	 * @param op operation of the message
	 */
	public Message() {
		j_message= new JSONObject();
	}
	
	public Object getParameter(String field) {
		return this.j_message.get(field);
	}
	
	/**
	 * Parse a variable number of parameters to a JSONObject
	 * @param params Variable number of strings of JSONObject. Format TYPE:CONTENT
	 */
	@SuppressWarnings("unchecked")
	public void setParameters(String ...params) {
		for (String param: params) {
			String[] components = param.split(":", 2);
			
			j_message.put(components[0],components[1]);
		}
	}
	
	public String toString() {
		return j_message.toJSONString();
	}
	
	@SuppressWarnings("unchecked")
	public Message translate(String from, String to) {
		
		String body= this.getParameter("BODY").toString();
		JSONParser parser= new JSONParser();
		try {
			//Creating URL
			String url_tail = "text="+URLEncoder.encode(body, "UTF-8")+"&lang="+from+"-"+to;
			URL full_url= new URL(util.Config.TRANSLATOR_URL+url_tail);
			
			//Opening connection
			URLConnection currentConnection= full_url.openConnection();
			
			//Reading
			BufferedReader fromRest= new BufferedReader(new InputStreamReader(currentConnection.getInputStream()));
			
			String line=null;
			StringBuffer sb= new StringBuffer();
			
			//Copying
			while((line=fromRest.readLine())!=null) {
				sb.append(line);
			}
			
			//Parsing and putting into the message
			JSONObject temp= (JSONObject) parser.parse(sb.toString());
			String new_body = temp.get("text").toString().replace("[\"", "").replace("\"]", "");
			j_message.put("BODY", new_body);
		} catch (IOException | ParseException e) {
			System.out.println("Error while translating the message");
		} 
		return this;
	}
	
	public Message parseToMessage(String str) {
		JSONParser parser= new JSONParser();
		try {
			JSONObject message= (JSONObject) parser.parse(str);
			this.j_message=message;
			return this;
		} catch (ParseException e) {
			return null;
		}
	}

	public static Message shallowCopy(Message message) {
		Message copied;
		if (message instanceof RequestMessage) {
			copied= new RequestMessage();
		}
		else {
			copied= new ResponseMessage();
		}
		copied.setJson(message.getJson());
		return copied;
	}
	
	private void setJson(JSONObject j_message) {
		this.j_message = j_message;
	}
	
	private JSONObject getJson() {
		return this.j_message;
	}
}
