package ca.masonx.jircc.chatabs;

import java.io.IOException;

public class IRCUser {
	private String nickname;
	private final String ident;
	private final String realname;
	private IRCServer myServer;
	
	public IRCUser(String nickname) {
		this.nickname = nickname;
		this.ident = nickname;
		this.realname = nickname;
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
		myServer.send("NICK " + nickname);
	}
	
	public void connectToServer(IRCServer server) throws IOException {
		myServer = server;
		myServer.send("NICK " + nickname);
		/* USER <ident> <mode> * :<realname> */
		myServer.send("USER " + ident + " 8 * :" + realname);
	}
	
	public void disconnectFromServer() throws IOException {
		myServer.send("QUIT :Bye!");
	}
	
	public void disconnectFromServer(String reason) throws IOException {
		myServer.send("QUIT :" + reason);
	}
	
	public void sendMessage(String location, String message) throws IOException {
		myServer.send("PRIVMSG " + location + " :" + message);
	}
}
