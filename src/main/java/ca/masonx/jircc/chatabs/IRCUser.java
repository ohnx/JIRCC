package ca.masonx.jircc.chatabs;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class IRCUser {
	private String nickname;
	private final String ident;
	private final String realname;
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
	
	public void joinChannel(String channel) throws IOException {
		if (channel.charAt(0) != '#') return;
		IRCBuffer buf = buffers.get(channel);
		
		if (buf == null) {
			buf = new IRCBuffer(channel);
			buffers.put(channel, buf);
		}
		myServer.send("JOIN " + channel);
	}
	
	public void addNamesToBuffer(String channel, String names[]) {
		IRCBuffer buf = buffers.get(channel);
		
		if (buf == null) {
			buf = new IRCBuffer(channel);
			buffers.put(channel, buf);
		}
		
		for (String name : names) {
			/* when a user is voiced or opped, their nick starts with a @ or +; we do not want this in our listings */
			if (name.charAt(0) == '@' || name.charAt(0) == '+') name = name.substring(1);
			if (name.equals(nickname)) continue; /* skip ourselves */
			buf.addParticipant(name);
		}
	}
	
	public void addMessageToBuffer(String channel, ChatMessage cm) {
		String bufname = channel;
		if (channel.equalsIgnoreCase("_")) {
			/* add it to all! */
			for (Entry<String, IRCBuffer> ib : buffers.entrySet()) {
				ib.getValue().addChat(cm);;
			}
			return;
		} else if (channel.charAt(0) != '#' && !cm.sender.equals(nickname)) {
			/* Check if this is a private message; if it is, we need to swap the buffer name */
			bufname = cm.sender;
		}
		
		IRCBuffer buf = buffers.get(bufname);
		
		if (buf == null) {
			buf = new IRCBuffer(bufname);
			buf.addChat(cm);
			buffers.put(bufname, buf);
		} else {
			buf.addChat(cm);
		}
		
		System.out.println("Buffer is now:");
		
		for (Entry<String, IRCBuffer> ib : buffers.entrySet()) {
			System.out.println("------\n" + ib.getKey() + ":\n" + ib.getValue());
		}
		
		System.out.println("------");
	}
}
