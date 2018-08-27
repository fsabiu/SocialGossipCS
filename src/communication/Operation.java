package communication;
import java.util.*;

public enum Operation {
	// User
	REGISTER(1),
	UNREGISTER(2),
	
	// Connections
	LOGIN(5),
	LOGOUT(6),
	
	// Single operations
	LOOKUP(10),
	FRIENDSHIP(11),
	LIST_FRIENDS(12),
	
	// Chatroom operations
	CHAT_CREATION(20),
	CHAT_ADDING(21),
	CHAT_LISTING(22),
	CHAT_CLOSING(23),
	
	// Messages
	MSG_TO_FRIEND(30),
	MSG_TO_CHATROOM(31),
	
	// Files
	FILE_TO_FRIEND(40),
	
	/*Notifications
	NOTIFY_ONLINE_FRIEND(50),
	NOTIFY_FRIENDSHIP(51),
	NOTIFY_SUBSCRIPTION(52),
	NOTIFY_CHATROOM_CLOSED(53),
	NOTIFY_CHATROOM_MESSAGE(54),
	NOTIFY_FILE_TO_FRIEND(55),
	*/
	
	// Server replies
	OK(200),
	ERR(500),
	USER_ALREADY_EXISTS(501),
	USER_DOES_NOT_EXIST(502),
	CHATROOM_ALREADY_EXISTS(503),
	CHATROOM_DOES_NOT_EXIST(504),
	PERMISSION_DENIED(505),
	USER_OFFLINE(506),
	INVALID_CREDENTIALS(507);
	
	private Long value;
	private static Map<Long, Operation> map= new HashMap<Long,Operation>();
	
	private Operation(int value) {
		this.value=new Long(value);
	}
	
	static {
		for (Operation op : Operation.values()) {
			map.put(op.value, op);
		}
	}
	
	public Long getValue() {
		return this.value;
	}
	
	public static Operation valueOf(Long op) {
		return (Operation) map.get(op);
	}

}
