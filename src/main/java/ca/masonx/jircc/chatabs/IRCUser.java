package ca.masonx.jircc.chatabs;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class IRCUser {
	private String nickname;
	/* ident and realname cannot change once connected to the server */
	private final String ident;
	private final String realname;
	private boolean buffersHaveChanged = false;
	private IRCServer myServer;
	private HashMap<String,IRCBuffer> buffers;
	
	public IRCUser(String nickname) {
		this.nickname = nickname;
		this.ident = nickname;
		this.realname = nickname;
		buffers = new HashMap<String,IRCBuffer>();
	}
	
	public IRCUser(String nickname, String ident, String realname) {
		this.nickname = nickname;
		this.ident = ident;
		this.realname = realname;
	}
	
	public IRCBuffer getBuffer(String name) {
		if (buffers.containsKey(name)) {
			return buffers.get(name);
		} else {
			IRCBuffer buf = new IRCBuffer(name);
			buffers.put(name, buf);
			return buf;
		}
	}
	
	public String[] getBuffers() {
		return buffers.keySet().toArray(new String[buffers.size()]);
	}
	
	public boolean haveBuffersChanged() {
		if (buffersHaveChanged) {
			buffersHaveChanged = false;
			return true;
		} else {
			return false;
		}
	}
	
	protected IRCBuffer createNewBuffer(String name) {
		IRCBuffer buf = buffers.get(name);
		
		if (buf == null) {
			buf = new IRCBuffer(name);
			buffers.put(name, buf);
			buffersHaveChanged = true;
		}
		
		return buf;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void changeNickname(String nickname) throws IOException {
		this.nickname = nickname;
		/* NICK <nickname> */
		myServer.send("NICK " + nickname);
	}
	
	public void connectToServer(IRCServer server) throws IOException {
		myServer = server;
		changeNickname(nickname);
		/* USER <ident> <mode> * :<realname> */
		myServer.send("USER " + ident + " 8 * :" + realname);
	}
	
	public void disconnectFromServer() throws IOException {
		disconnectFromServer("Bye!");
	}
	
	public void disconnectFromServer(String reason) throws IOException {
		/* QUIT :<reason> */
		myServer.send("QUIT :" + reason);
	}
	
	public void sendMessage(String where, String message) throws IOException {
		/* PRIVMSG <where> :<message> */
		myServer.send("PRIVMSG " + where + " :" + message);
		
		ChatMessage cm = new ChatMessage(message, nickname, new Date(), ChatType.CHAT_MESSAGE);
		
		addMessageToBuffer(where, cm);		
	}
	
	public void sendNotice(String where, String message) throws IOException {
		/* PRIVMSG <where> :<message> */
		myServer.send("NOTICE " + where + " :" + message);
		
		ChatMessage cm = new ChatMessage(message, nickname, new Date(), ChatType.CHAT_MESSAGE);
		
		addMessageToBuffer(where, cm);		
	}
	
	public void sendActionMessage(String where, String message) throws IOException {
		/* PRIVMSG <where> :\u0001ACTION <message>\u0001 */
		myServer.send("PRIVMSG " + where + " :\u0001ACTION " + message + "\u0001");
		
		ChatMessage cm = new ChatMessage(message, nickname, new Date(), ChatType.ACTION_MESSAGE);
		
		addMessageToBuffer(where, cm);	
	}
	
	public void sendRaw(String message) throws IOException {
		myServer.send(message);
	}
	
	public void joinChannel(String channel) throws IOException {
		if (channel.charAt(0) != '#') return;
		
		myServer.send("JOIN " + channel);
		createNewBuffer(channel);
	}
	
	public void addNamesToBuffer(String channel, String names[]) {
		IRCBuffer buf = createNewBuffer(channel);
		
		for (String name : names) {
			/* when a user is voiced or opped, their nick starts with a @ or +; we do not want this in our listings */
			if (name.charAt(0) == '@' || name.charAt(0) == '+') name = name.substring(1);
			if (name.equals(nickname)) continue; /* skip ourselves */
			buf.addParticipant(name);
		}
	}
	
	public void addMessageToBuffer(String channel, ChatMessage cm) {
		String bufname = channel;
		if (channel.equals("_")) {
			/* add it to all! */
			for (Entry<String, IRCBuffer> ib : buffers.entrySet()) {
				ib.getValue().addChat(cm);;
			}
			return;
		} else if (channel.equals("*")) {
			/* special */
		} else if (channel.charAt(0) != '#' && !cm.sender.equals(nickname)) {
			/* Check if this is a private message; if it is, we need to swap the buffer name */
			bufname = cm.sender;
		}
		
		IRCBuffer buf = createNewBuffer(bufname);
		buf.addChat(cm);
		/* Debug code.
		System.out.println("Buffer is now:");
		
		for (Entry<String, IRCBuffer> ib : buffers.entrySet()) {
			System.out.println("------\n" + ib.getKey() + ":\n" + ib.getValue());
		}
		
		System.out.println("------");*/
	}
}
