package ca.masonx.jircc.chatabs;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

public class IRCBuffer implements Serializable {
	private static final long serialVersionUID = -7566709593901748331L;
	private CopyOnWriteArrayList<ChatMessage> chats;
	private CopyOnWriteArrayList<String> participants;
	private boolean hasChanged = false;
	private boolean hasParticipantsChanged = false;
	public final String name;
	
	public IRCBuffer(String name) {
		chats = new CopyOnWriteArrayList<ChatMessage>();
		participants = new CopyOnWriteArrayList<String>();
		this.name = name;
	}
	
	public String[] getChats() {
		String arr[] = new String[chats.size()];
		int i = 0;
		
		for (ChatMessage cm : chats) {
			arr[i] = cm.toString();
			i++;
		}
		
		return arr;
	}
	
	public int length() {
		return chats.size();
	}
	
	public String[] getChats(int start, int end) { /* inclusive on both ends */
		String arr[] = new String[end - start + 1];
		int i = -1;
		
		for (ChatMessage cm : chats) {
			i++;
			if (i < start) continue;
			if (i > end) break;
			arr[i - start] = cm.toString();
		}
		
		return arr;
	}
	
	public void addParticipant(String pName) {
		participants.add(pName);
	}
	
	public String[] getParticipants() {
		return participants.toArray(new String[participants.size()]);
	}

	public void addChat(ChatMessage cm) {
		switch (cm.ct) {
		case JOIN_MESSAGE:
			participants.add(cm.sender);
			hasParticipantsChanged = true;
			break; /* add this message to the buffer */
		case PART_MESSAGE:
		case KICK_MESSAGE:
			participants.remove(cm.sender);
			hasParticipantsChanged =  true;
			break; /* add this message to the buffer */
		case NICK_MESSAGE:
		case QUIT_MESSAGE:
			if (participants.contains(cm.sender)) {
				participants.remove(cm.sender);
				if (cm.ct == ChatType.NICK_MESSAGE) participants.add(cm.message); /* add the new nick */
				hasParticipantsChanged = true;
				break;
			}
			else return;
			/* only add this message to the buffer if the user described is in this buffer */
		case CHAT_MESSAGE:
		case MODE_MESSAGE:
		case TOPIC_MESSAGE:
		case UNKNOWN_MESSAGE:
			break; /* add this message to the buffer */
		default:
			break;
		}
		chats.add(cm);
		hasChanged = true;
	}
	
	public boolean hasChanged() {
		if (hasChanged) {
			hasChanged = false;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasParticipantsChanged() {
		if (hasParticipantsChanged) {
			hasParticipantsChanged = false;
			return true;
		} else {
			return false;
		}
	}
	
	public String toString() {
		String ret = "";
		for (ChatMessage cm : chats) {
			ret += cm + "\n";
		}
		return ret;
	}
}
