package ca.masonx.jircc.tasks;

import java.io.IOException;
import java.net.UnknownHostException;

import ca.masonx.jircc.chatabs.ChatType;
import ca.masonx.jircc.chatabs.IRCServer;
import ca.masonx.jircc.chatabs.IRCUser;
import ca.masonx.jircc.chatabs.InputParser;
import ca.masonx.jircc.chatabs.NotChatMessageException;
import ca.masonx.jircc.chatabs.InputParser.ParserResult;

public class NetConnection implements Runnable {
	public final IRCServer server;
	public final IRCUser user;
	
	public NetConnection(String server, String nickname) {
		this.server = new IRCServer(server);
		this.user = new IRCUser(nickname);
	}
	
	public boolean hasBufferChanged(String name) {
		return user.getBuffer(name).hasChanged();
	}
	
	public void run() {
		String line;
		ParserResult pr;
		
		try {
			server.connect();
			user.connectToServer(server);
			
			while ((line = server.getLine()) != null) {
				try {
					/* check if this is a chat message */
					pr = InputParser.parseChatMessage(line);
					System.out.println("*** Chan:`" + pr.channel + "`" + ", " + pr.message);
					
					if (pr.message.ct == ChatType.UNKNOWN_MESSAGE) {
						/* login successful */
						if (pr.message.message.contains("005")) {
							user.joinChannel("#ohnxsecret");
						} else if (pr.message.message.contains("353")) {
			            	// this is a NAMES listing
			            	/* ie, :orwell.freenode.net 353 ohnx- = #channel :ohnx- names... */
			            	String ircnames[] = line.split(" :");
			            	String intermediate[] = ircnames[0].split(" ");
			            	String ircchan = intermediate[intermediate.length - 1];
			            	user.addNamesToBuffer(ircchan, ircnames[1].split(" "));
			            } else if (pr.message.message.contains("433")) {
			            	/* nickname in use */
							System.out.println("** Nickname is already in use, trying another.");
							user.changeNickname(user.getNickname()+"-");
						}
					} else {
						if (pr.message.message.contains("JOINCHAN")) {
							user.joinChannel("##ohnxsecret");
						} else if (pr.message.message.contains("BYE")) {
							user.disconnectFromServer("\"Bye, friends!\"");
						}
					}
					
					user.addMessageToBuffer(pr.channel, pr.message);
				} catch (NotChatMessageException e) {
					/* not a chat message */
					if (line.toUpperCase().startsWith("PING ")) {
		                // We must respond to PINGs to avoid being disconnected.
		                server.send("PONG " + line.substring(5));
		            }
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
