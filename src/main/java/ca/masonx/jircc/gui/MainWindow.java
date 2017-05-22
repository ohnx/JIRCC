package ca.masonx.jircc.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import ca.masonx.jircc.tasks.NetConnection;

public class MainWindow {
	/* variable storage */
	protected RootWindowFrame rwf;
	protected NetConnection nc;
	protected Thread ncThread;
	
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
		
        rwf.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
            	System.out.println("Caught something here");
            	/* when the window is closed, the code will fallthrough to here and allow nice disconnect from server */
        		if (nc.isConnected()) {
        			nc.disconnect();
        		}
            }
        } );
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
					!rwf.monitoringChanged()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			
			/* update buffer items */
			if (!rwf.getMonitoring().equals(monitoring)) {
				lastIndex = 0;
				rwf.clearMessagesItems();
				monitoring = rwf.getMonitoring();
				shouldUpdate = true;
				rwf.setParticipantsItems(nc.user.getBuffer(monitoring).getParticipants());
				System.out.println("Caught change!");
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
