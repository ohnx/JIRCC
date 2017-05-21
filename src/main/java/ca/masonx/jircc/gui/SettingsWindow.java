package ca.masonx.jircc.gui;

import javax.swing.JFrame;

public class SettingsWindow {
	public final String server;
	public final String nick;
	public final boolean shouldQuit;
	
	public SettingsWindow(String dserver, String dnick) {
		ConfigWindowFrame cwf = new ConfigWindowFrame(dserver, dnick);
		cwf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cwf.setTitle("JIRCC Settings");
		cwf.setSize(250, 150);
		// Center the frame on screen
		cwf.setLocationRelativeTo(null);
		cwf.setVisible(true);
		
		while (true) {
			while (cwf.isVisible() && !cwf.hasConfigured()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {}
			}
			
			/* user has closed window, so we quit the entire app */
			if (!cwf.isVisible()) break;
			
			nick = cwf.getConfiguredNick();
			server = cwf.getConfiguredServer();
			shouldQuit = false;
			cwf.setVisible(false);
			return;
		}
		
		shouldQuit = true;
		server = "";
		nick = "";
	}
}
