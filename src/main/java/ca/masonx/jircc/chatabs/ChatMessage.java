package ca.masonx.jircc.chatabs;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = -8808284528039790127L;
	private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	public final String sender;
	public final String message;
	public final Date timeRecv;
	public final ChatType ct;
	
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
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " joined the channel.";
		case PART_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " left the channel (" + message + ").";
		case QUIT_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " quit the server (" + message + ").";
		case KICK_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " was kicked from the channel " + message + ".";
		case MODE_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " " + message + ".";
		case NICK_MESSAGE:
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " is now known as " + message + ".";
		case UNKNOWN_MESSAGE:
		default:
			return "[" + dateFormat.format(timeRecv) + "] " + message;
		}
	}
}
