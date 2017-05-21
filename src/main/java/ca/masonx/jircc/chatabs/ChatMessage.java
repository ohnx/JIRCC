/**
 * ChatMessage.java
 * 
 * Represents a Chat Message stored within an IRCBuffer.
 * 
 * Contains a ChatType to know how to toString() itself
 */
package ca.masonx.jircc.chatabs;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable {
	/* Serializable in case in the future I add the ability to write a buffer to a file */
	private static final long serialVersionUID = -8808284528039790127L;
	/* Date format */
	private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	/* Variables are public and final */
	public final String sender;
	public final String message;
	public final Date timeRecv;
	public final ChatType ct;
	
	/**
	 * ChatMessage instantiation method.
	 * 
	 * @param message The string message
	 * @param sender The sender of the message
	 * @param timeRecv The time the message was received
	 * @param ct The ChatType of the ChatMessage
	 */
	public ChatMessage(String message, String sender, Date timeRecv, ChatType ct) {
		this.message = message;
		this.sender = sender;
		this.timeRecv = timeRecv;
		this.ct = ct;
	}
	
	/**
	 * Convert the ChatMessage toString
	 * 
	 * @return the String representation of the ChatMessage
	 */
	public String toString() {
		switch (ct) {
		case CHAT_MESSAGE:
			/* format is [hh:mm:ss] <"sender"> "message" */
			return "[" + dateFormat.format(timeRecv) + "] <" + sender + "> " + message;
		case ACTION_MESSAGE:
			/* format is [hh:mm:ss] * "sender" "message" */
			return "[" + dateFormat.format(timeRecv) + "] * " + sender + " " + message;
		case JOIN_MESSAGE:
			/* format is [hh:mm:ss] "sender" joined the channel */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " joined the channel.";
		case PART_MESSAGE:
			/* format is [hh:mm:ss] "sender" left the channel ("message") */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " left the channel (" + message + ").";
		case QUIT_MESSAGE:
			/* format is [hh:mm:ss] "sender" quit the server ("message") */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " quit the server (" + message + ").";
		case KICK_MESSAGE:
			/* format is [hh:mm:ss] "sender" was kicked from the channel "message" -- note that message contains who kicked and why. */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " was kicked from the channel " + message + ".";
		case MODE_MESSAGE:
			/* format is [hh:mm:ss] "sender" changed mode: "modes" */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " changed mode: " + message + ".";
		case NICK_MESSAGE:
			/* format is [hh:mm:ss] "oldnick" is now known as "newnick" */
			return "[" + dateFormat.format(timeRecv) + "] " + sender + " is now known as " + message + ".";
		case TOPIC_MESSAGE:
			/* format is [hh:mm:ss] Topic for channel is "topic" */
			return "[" + dateFormat.format(timeRecv) + "] Topic for channel is: " + message;
		case UNKNOWN_MESSAGE:
		default:
			/* format is [hh:mm:ss] "message" */
			return "[" + dateFormat.format(timeRecv) + "] " + message;
		}
	}
}
