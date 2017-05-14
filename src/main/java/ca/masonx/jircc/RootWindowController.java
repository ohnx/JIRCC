package ca.masonx.jircc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.swing.JFrame;

import ca.masonx.jircc.chatabs.*;
import ca.masonx.jircc.chatabs.InputParser.ParserResult;
import ca.masonx.jircc.gui.RootWindowFrame;

public class RootWindowController {
	RootWindowFrame rwf;
	
	public static void main(String[] args) {
		IRCServer irctest = new IRCServer("chat.freenode.net:6667");
		IRCUser irctestuser = new IRCUser("ohnx");
		
		String line;
		
		try {
			irctest.connect();
			irctestuser.connectToServer(irctest);
						
			/* loop to log in */
			while ((line = irctest.getLine()) != null) {
				if (line.indexOf("004") >= 0) {
					// We are now logged in.
					break;
				} else if (line.indexOf("433") >= 0) {
					System.out.println("** Nickname is already in use.");
					irctestuser.changeNickname(irctestuser.getNickname()+"-");
				}
			}
			
			irctest.send("JOIN ##opmeplz");
						
			ParserResult pr;
			
			while ((line = irctest.getLine()) != null) {
				try {
					/* check if this is a chat message */
					pr = InputParser.parseChatMessage(line);
					System.out.println("*** Chan:`" + pr.channel + "`" + ", " + pr.message);
					
					irctestuser.addMessageToBuffer(pr.channel, pr.message);
				} catch (NotChatMessageException e) {
					/* not a chat message */
					if (line.toUpperCase().startsWith("PING ")) {
		                // We must respond to PINGs to avoid being disconnected.
		                irctest.send("PONG " + line.substring(5));
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
		
		/*
		RootWindowController rwc = new RootWindowController();
		rwc.openWindow();*/
	}
	
	private RootWindowController() {
		rwf = new RootWindowFrame();
		// Basic window properties
		rwf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rwf.setTitle("JIRCC");
		rwf.setSize(300,250);
		// Center frame
		rwf.setLocationRelativeTo(null);
	}

	@SuppressWarnings("unused")
	private void openWindow() {
		rwf.setVisible(true);
	}
}
