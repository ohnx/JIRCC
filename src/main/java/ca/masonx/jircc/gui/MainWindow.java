package ca.masonx.jircc.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;

import ca.masonx.jircc.UserInputParser;
import ca.masonx.jircc.chatabs.ChatMessage;
import ca.masonx.jircc.chatabs.ChatType;
import ca.masonx.jircc.tasks.NetConnection;

public class MainWindow {
	/* variable storage */
	protected RootWindowFrame rwf;
	protected NetConnection nc;
	protected Thread ncThread;
	protected UserInputParser uip;
	
	public MainWindow(String server, String nick) {
		rwf = new RootWindowFrame();
		// Basic window properties
		rwf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rwf.setTitle("JIRCC");
		rwf.setSize(600, 450);
		// Center the frame on screen
		rwf.setLocationRelativeTo(null);
		
		// Start NetConnection thread
		nc = new NetConnection(server, nick);
		ncThread = new Thread(nc);
		ncThread.start();
		
		// Init user input parser
		uip = new UserInputParser(nc);
		
		// Catch window closes
        rwf.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
            	/* when the window is closed, the code will fallthrough to here and allow nice disconnect from server */
        		if (nc.isConnected()) {
        			nc.disconnect();
        		}
            }
        });
	}
	
	/**
	 * Open the window and begin polling for events.
	 * 
	 * The event loop serves as a messaging method between classes.
	 */
	public boolean open() {
		rwf.setVisible(true);
		
		int lastIndex = 0, newIndex = -1;
		String monitoring = "*";
		String arrdm[];
		boolean shouldUpdate = false;
		
		while (true) {
			/* we monitor for events */
			while (!nc.hasBufferChanged(rwf.getMonitoring()) &&
					!nc.user.haveBuffersChanged() &&
					!nc.hasBufferParticipantsChanged(rwf.getMonitoring()) &&
					!rwf.monitoringChanged() &&
					!rwf.hasRequestedSend()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			
			/* check if sending something */
			if (rwf.hasRequestedSend()) {
				String parse = rwf.getSendString();
				try {
					if (uip.parseUserInput(parse, monitoring)) {
						rwf.clearSendString();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Failed to send the message!");
					nc.user.getBuffer("*").addChat(new ChatMessage("[ERROR] Failed to send chat message to server!", "", new Date(), ChatType.UNKNOWN_MESSAGE));
					e.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					
				}
			}
			
			/* update buffer items */
			if (!rwf.getMonitoring().equals(monitoring)) {
				lastIndex = 0;
				rwf.clearMessagesItems();
				monitoring = rwf.getMonitoring();
				shouldUpdate = true;
				rwf.setParticipantsItems(nc.user.getBuffer(monitoring).getParticipants());
			}
			newIndex = nc.user.getBuffer(monitoring).length();
			if (shouldUpdate || newIndex != lastIndex) {
				arrdm = nc.user.getBuffer(monitoring).getChats(lastIndex, newIndex - 1);
				lastIndex = newIndex;
				rwf.updateMessagesItems(arrdm);
				shouldUpdate = false;
			} else { /* likely the buffers changed or participants changed */
				rwf.setBuffersItems(nc.user.getBuffers());
				rwf.setParticipantsItems(nc.user.getBuffer(monitoring).getParticipants());
			}
		}
	}
}
