/**
 * NetConnection.java
 * 
 * The NetConnection class runs as a thread, separate from
 * the GUI logic. 
 */
package ca.masonx.jircc.tasks;

import java.io.IOException;
import java.net.UnknownHostException;

import ca.masonx.jircc.RootApplicationController;
import ca.masonx.jircc.chatabs.ChatMessage;
import ca.masonx.jircc.chatabs.ChatType;
import ca.masonx.jircc.chatabs.IRCServer;
import ca.masonx.jircc.chatabs.IRCUser;
import ca.masonx.jircc.chatabs.ServerInputParser;
import ca.masonx.jircc.chatabs.NotChatMessageException;
import ca.masonx.jircc.chatabs.ServerInputParser.ParserResult;

public class NetConnection implements Runnable {
	/* Variables */
	public final IRCServer server;
	public final IRCUser user;
	private boolean isConnected = true;
	
	public NetConnection(String server, String nickname) {
		this.server = new IRCServer(server);
		this.user = new IRCUser(nickname);
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public void connect() {
		try {
			server.connect();
			isConnected = true;
			user.connectToServer(server);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			user.disconnectFromServer();
			server.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean hasBufferChanged(String name) {
		return user.getBuffer(name).hasChanged();
	}
	
	public boolean hasBufferParticipantsChanged(String name) {
		return user.getBuffer(name).hasParticipantsChanged();
	}
	
	/**
	 * NetConnection main code.
	 * 
	 * Poll the server for input and parse that input appropriately.
	 */
	public void run() {
		String line;
		ParserResult pr;
		boolean addToBuffer = true;
		
		try {
			connect();
			while ((line = server.getLine()) != null) {
				try {
					/* check if this is a chat message */
					pr = ServerInputParser.parseChatMessage(line);
					System.out.println("*** Chan:`" + pr.channel + "`" + ", " + pr.message);
					
					/* not one of the types of chat messages */
					if (pr.message.ct == ChatType.UNKNOWN_MESSAGE) {
						/* login successful, join whatever initial channels the user wants to join */
						if (pr.message.message.contains("005")) {
							user.joinChannel("#ohnxsecret");
						} else if (pr.message.message.contains("353")) {
			            	/* this is a NAMES listing for a channel, so we parse it.
			            	 * 
			            	 * ie, :orwell.freenode.net 353 ohnx- = #channel :ohnx- names...
			            	 */
			            	String ircnames[] = line.split(" :");
			            	String intermediate[] = ircnames[0].split(" ");
			            	String ircchan = intermediate[intermediate.length - 1];
			            	user.addNamesToBuffer(ircchan, ircnames[1].split(" "));
			            	addToBuffer = false;
			            } else if (pr.message.message.contains("332")) {
			            	// this is a TOPIC listing
			            	user.addMessageToBuffer(pr.message.message.split(" ", 5)[3],
			            			new ChatMessage(pr.message.message.split(" :", 2)[1], "unknown", pr.message.timeRecv, ChatType.TOPIC_MESSAGE));
			            	addToBuffer = false;
			            } else if (pr.message.message.contains("433")) {
			            	/* nickname in use */
							System.out.println("** Nickname is already in use, trying another.");
							user.changeNickname(user.getNickname()+"-");
						}
					} else if (pr.message.ct == ChatType.VERSION_MESSAGE) {
						/* Send CTCP VERSION reply */
						user.sendNotice(pr.message.sender, "\u0001VERSION "+RootApplicationController.version+"\u0001");
					} else {
						if (pr.message.message.contains("JOINCHAN")) {
							user.joinChannel("#freenode");
						} else if (pr.message.message.contains("BYE")) {
							disconnect();
						} else if (pr.message.message.contains("TESTS")) {
							user.sendActionMessage("#ohnxsecret", "testing things!");
						}
					}
					
					if (addToBuffer) user.addMessageToBuffer(pr.channel, pr.message);
					addToBuffer = true;
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
			isConnected = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isConnected = false;
		}

	}

}
