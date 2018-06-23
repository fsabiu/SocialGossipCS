package communication;

public class NotificationMessage extends Message {
		@SuppressWarnings("unchecked")
		public NotificationMessage(Operation op) {
			super();
			message.put("OPERATION", op);
		}
}
