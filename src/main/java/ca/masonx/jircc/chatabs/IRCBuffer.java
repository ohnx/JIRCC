package ca.masonx.jircc.chatabs;

import java.io.Serializable;
import java.util.ArrayList;

public class IRCBuffer implements Serializable {
	private static final long serialVersionUID = -7566709593901748331L;
	private ArrayList<ChatMessage> chats;
	private ArrayList<String> participants;
	public final String name;
	
	public IRCBuffer(String name) {
		chats = new ArrayList<ChatMessage>();
		participants = new ArrayList<String>();
		this.name = name;
	}
	
	protected ArrayList<ChatMessage> getChats() {
		return chats;
	}
	
	public void addParticipant(String pName) {
		participants.add(pName);
	}
	
	public void addChat(ChatMessage cm) {
		switch (cm.ct) {
		case JOIN_MESSAGE:
			participants.add(cm.sender);
			break; /* add this message to the buffer */
		case PART_MESSAGE:
		case KICK_MESSAGE:
			participants.remove(cm.sender);
			break; /* add this message to the buffer */
		case NICK_MESSAGE:
		case QUIT_MESSAGE:
			if (participants.contains(cm.sender)) break;
			else return;
			/* only add this message to the buffer if the user described is in this buffer */
		case CHAT_MESSAGE:
		case MODE_MESSAGE:
			break; /* add this message to the buffer */
		case UNKNOWN_MESSAGE:
			return; /* do not add this message to the buffer */
		}
		chats.add(cm);
	}
	
	public String toString() {
		String ret = "";
		for (ChatMessage cm : chats) {
			ret += cm + "\n";
		}
		System.out.println("My participants: " + participants);
		return ret;
	}
}
