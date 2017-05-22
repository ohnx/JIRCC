package ca.masonx.jircc;

import java.io.IOException;

import ca.masonx.jircc.tasks.NetConnection;

public class UserInputParser {
	private final NetConnection nc;
	
	public UserInputParser(NetConnection nc) {
		this.nc = nc;
	}
	
	public boolean parseUserInput(String input, String currentchan) throws IOException, ArrayIndexOutOfBoundsException {
		if (input.charAt(0) == '/') { // command
			input = input.substring(1, input.length());
			String[] inputargs = input.split(" ", 2);
			String command = inputargs[0].toLowerCase();
			
			if (command.equals("me")) {
				nc.user.sendActionMessage(currentchan, inputargs[1]);
			} else if (command.equals("quote")) {
				nc.user.sendRaw(inputargs[1]);
			} else if (command.equals("msg") || command.equals("privmsg")) {
				String[] newsplit = inputargs[1].split(" ", 2);
				nc.user.sendMessage(newsplit[0], newsplit[1]);
			} else if (command.equals("whois")) {
				nc.user.sendRaw("WHOIS " + inputargs[1]);
			} else if (command.equals("join")) {
				nc.user.joinChannel(inputargs[1]);
			} else if (command.equals("part")) {
				// TODO: NYI
			} else if (command.equals("mode")) {
				// TODO: NYI
			} else {
				return false;
			}
		} else { // just a chat message
			nc.user.sendMessage(currentchan, input);
		}
		return true;
	}
}
