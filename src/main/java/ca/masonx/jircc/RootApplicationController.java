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
		rwf.setSize(300,250);
		// Center frame
		rwf.setLocationRelativeTo(null);
		
		nc = new NetConnection("irc.esper.net:6667", "somethingohnxbot");
		
		new Thread(nc).start();
	}

	private void openWindow() {
		rwf.setVisible(true);
		
		int lastIndex = 0, newIndex = 0;
		String monitoring = "#ohnxsecret";
		String arrdm[];
		
		while (rwf.isVisible()) {
			while (!nc.hasBufferChanged(monitoring)) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			newIndex = nc.user.getBuffer(monitoring).length();
			arrdm = nc.user.getBuffer(monitoring).getChats(lastIndex, newIndex - 1);
			lastIndex = newIndex;
			rwf.updateBufferItems(arrdm);
		}
	}
}
