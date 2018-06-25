package communication;

public class NotificationMessage extends Message {
		@SuppressWarnings("unchecked")
		public NotificationMessage() {
			super();
			j_message.put("TYPE", "notification");
		}
}
