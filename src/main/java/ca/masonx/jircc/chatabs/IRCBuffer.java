package ca.masonx.jircc.chatabs;

import java.io.Serializable;
import java.util.ArrayList;

public class IRCBuffer implements Serializable {
	private static final long serialVersionUID = -7566709593901748331L;
	private ArrayList<ChatMessage> chats;
	public final String name;
	
	public IRCBuffer(String name) {
		chats = new ArrayList<ChatMessage>();
		this.name = name;
	}
	
	protected ArrayList<ChatMessage> getChats() {
		return chats;
	}
	
	public void addChat(ChatMessage cm) {
		chats.add(cm);
	}
	
	public String toString() {
		String ret = "";
		for (ChatMessage cm : chats) {
			ret += cm + "\n";
		}
		return ret;
	}
}
