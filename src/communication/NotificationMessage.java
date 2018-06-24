package communication;

public class NotificationMessage extends Message {
		@SuppressWarnings("unchecked")
		public NotificationMessage() {
			super();
			message.put("TYPE", "notification");
		}
}
