package ca.masonx.jircc;

import javax.swing.JFrame;

import ca.masonx.jircc.gui.RootWindowFrame;
import ca.masonx.jircc.tasks.NetConnection;

public class RootApplicationController {
	RootWindowFrame rwf;
	NetConnection nc;
	
	public static void main(String[] args) {
		RootApplicationController rwc = new RootApplicationController();
		rwc.openWindow();
	}
	
	private RootApplicationController() {
		rwf = new RootWindowFrame();
		// Basic window properties
		rwf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rwf.setTitle("JIRCC");
		rwf.setSize(550, 450);
		// Center frame
		rwf.setLocationRelativeTo(null);
		
		nc = new NetConnection("irc.freenode.net:6667", "actuallynotohnx");
		
		new Thread(nc).start();
	}

	private void openWindow() {
		rwf.setVisible(true);
		
		int lastIndex = 0, newIndex = -1;
		String monitoring = "*";
		String arrdm[];
		boolean shouldUpdate = false;
		
		while (rwf.isVisible()) {
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
