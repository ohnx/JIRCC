package ca.masonx.jircc.chatabs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {
	private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private final String sender;
	private final String message;
	private final Date timeRecv;
	private final ChatType ct;
	
	public ChatMessage(String message, String sender, Date timeRecv, ChatType ct) {
		this.message = message;
		this.sender = sender;
		this.timeRecv = timeRecv;
		this.ct = ct;
	}
	
	public String toString() {
		switch (ct) {
		case CHAT_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] <" + sender + "> " + message;
		case JOIN_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] +" + sender;
		case PART_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] -" + sender;
		case QUIT_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] -" + sender;
		case UNKNOWN_MESSAGE:
		default:
			return "[" + dateFormat.format(timeRecv) + "] " + message;
		}
	}
}
