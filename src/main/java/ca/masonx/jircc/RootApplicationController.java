/**
 * RootApplicationController.java
 * 
 * The RootApplicationController contains the entry point for the application.
 * It serves a purpose of initial setup for other classes. It opens the main
 * GUI interface and also starts the thread for the network connection.
 */
package ca.masonx.jircc;

import ca.masonx.jircc.gui.MainWindow;
import ca.masonx.jircc.gui.SettingsWindow;

public class RootApplicationController {
	public static String version = "JIRCC v0.01-SNAPSHOT";
	/**
	 * Main entry point for application.
	 * @param args Command-line args (ignored at the moment)
	 */
	public static void main(String[] args) {
		new RootApplicationController().mainApplicationLoop();
		/* Fall through here, application ends here */
	}
	
	/**
	 * Main application loop - open the settings dialog, 
	 */
	protected void mainApplicationLoop() {
		String server = "irc.freenode.net:6667";
		String nick = ""; // TODO: Make this the system username
		
		/* open the settings window */
		SettingsWindow sw = new SettingsWindow(server, nick);
		
		/* Get results from SettingsWindow */
		server = sw.server;
		nick = sw.nick;
			
		/* create and open the main window */
		new MainWindow(server, nick).open();
		
		/* Application ends here. */
	}
}
