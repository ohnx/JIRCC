package ca.masonx.jircc.chatabs;

import java.util.Date;

public class InputParser {
	public static ParserResult parseChatMessage(String line) throws NotChatMessageException, ArrayIndexOutOfBoundsException {
		Date now = new Date();
		String message = "";
		String sender = "";
		String channel = "";
		ChatType ct = ChatType.UNKNOWN_MESSAGE;
		
		/* make sure this is valid IRC receive message */
		if (line.charAt(0) != ':') {
			throw new NotChatMessageException("Received data from server that does not begin with `:'.");
		}
		
		line = line.substring(1);
		String split[] = line.split(" ", 3);
		
		if (split[1].equals("PRIVMSG") || split[1].equals("NOTICE")) {
			/*
			 * Sample message: (after removing first char)
			 * ohnx!~ohnx@unaffiliated/ohnx PRIVMSG #/ :hello!
			 * 
			 * split(3) will produce
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "PRIVMSG", "#/ :hello"]
			 */
			sender = split[0].split("!", 2)[0]; /* part before the "!" is nick */
			
			/* split the <channel name> :<message> into its two parts */
			String chanmess[] = split[2].split(" :", 2);
			channel = chanmess[0];
			message = chanmess[1];
			ct = ChatType.CHAT_MESSAGE;
			
			if (message.contains("\u0001ACTION")) {
				/* remove leading "\0x01CTCP ACTION " and trailing "0x01" */
				message = message.substring(8, message.length() - 1);
				ct = ChatType.ACTION_MESSAGE;
			}
		} else if (split[1].equals("PART")) {
			/*
			 * Sample messages: (after removing first char)
			 * ohnx!~ohnx@unaffiliated/ohnx PART #/ :"bye"
			 * ohnx!~ohnx@unaffiliated/ohnx PART #/
			 * 
			 * split(3) will produce
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "PART", "#/ :bye"]
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "PART", "#/"]
			 */
			sender = split[0].split("!", 2)[0]; /* part before the "!" is nick */
			
			/* split the <channel name> :<message> into its two parts */
			String chanmess[] = split[2].split(" :", 2);
			channel = chanmess[0];
			
			/* sometimes the server will not send a part reason. */
			if (chanmess.length == 2) message = chanmess[1];
			
			ct = ChatType.PART_MESSAGE;
		} else if (split[1].equals("JOIN")) {
			/*
			 * Sample message: (after removing first char)
			 * ohnx!~ohnx@unaffiliated/ohnx JOIN #/
			 * 
			 * split(3) will produce
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "JOIN", "#/"]
			 */
			sender = split[0].split("!", 2)[0]; /* part before the "!" is nick */
			
			/* some servers may send a join reason, so we cut it off. */
			String chanmess[] = split[2].split(" ", 2);
			
			channel = chanmess[0];
			message = "User joined the channel";
			ct = ChatType.JOIN_MESSAGE;
		} else if (split[1].equals("KICK")) {
			/*
			 * Sample message: (after removing first char)
			 * ohnx!~ohnx@unaffiliated/ohnx KICK #/ nick1 :cya
			 * 
			 * split(3) will produce
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "KICK", "#/ nick1 :cya"]
			 * 
			 * If we split(3) split[2], we get:
			 * ["#/" (channel), "nick1" (nick), ":cya", (kick reason)]
			 */
			String moresplit[] = split[2].split(" ", 3);
			sender = moresplit[1]; /* this name is a bit ambiguous, "sender" is the person who got kicked */
			channel = moresplit[0];
			message = "by " + split[0].split("!", 2)[0] + " (" + moresplit[2].substring(1) + ")"; /* remove the leading colon from kick reason */
			ct = ChatType.KICK_MESSAGE;
		} else if (split[1].equals("MODE")) {
			/*
			 * Sample messages: (after removing first char)
			 * ohnx!~ohnx@unaffiliated/ohnx MODE ##opmeplz +o ohnx-
			 * ohnx!~ohnx@unaffiliated/ohnx MODE ##opmeplz +ov ohnx- nick
			 * ohnx!~ohnx@unaffiliated/ohnx MODE ##opmeplz +m
			 * ohnx- MODE ohnx- :+i
			 * 
			 * split(3) will produce
			 * ["ohnx!~ohnx@unaffiliated/ohnx", "MODE", "##opmeplz +o ohnx-"]
			 */
			String moresplit[] = split[2].split(" ", 2); /* split into the channel and the operation done */
			sender = split[0].split("!", 2)[0];
			channel = moresplit[0];
			message = moresplit[1].charAt(0) == ':' ? moresplit[1].substring(1) : moresplit[1]; /* remove the leading colon if it is there */
			ct = ChatType.MODE_MESSAGE;
		} else if (split[1].equals("QUIT")) {

			/*
			 * Sample message: (after removing first char)
			 * nick1!somebody@1.2.3.4 QUIT :Client Quit
			 * 
			 * split(3) will produce
			 * ["nick1!somebody@1.2.3.4", "QUIT", ":Client Quit"]
			 */
			sender = split[0].split("!", 2)[0]; /* part before the "!" is old nick */
						
			channel = "_"; /* We do not actually immediately know what channels this is in */
			message = split[2].substring(1); /* remove the leading colon from quit message */
			ct = ChatType.QUIT_MESSAGE;
		} else if (split[1].equals("NICK")) {
			/*
			 * Sample message: (after removing first char)
			 * nick1!somebody@1.2.3.4 NICK :nick2
			 * 
			 * split(3) will produce
			 * ["nick1!somebody@1.2.3.4", "NICK", ":nick2"]
			 */
			sender = split[0].split("!", 2)[0]; /* part before the "!" is old nick */
						
			channel = "_"; /* We do not actually immediately know what channels this is in */
			message = split[2].substring(1); /* remove the leading colon from new nick */
			ct = ChatType.NICK_MESSAGE;
		} else {
			return new ParserResult("*", new ChatMessage(line, split[0], now, ChatType.UNKNOWN_MESSAGE));
			// throw new NotChatMessageException("Not a chat message!");
		}
		
		return new ParserResult(channel, new ChatMessage(message, sender, now, ct));
	}
	
	public static class ParserResult {
		public final String channel;
		public final ChatMessage message;
		
		public ParserResult(String channel, ChatMessage message) {
			this.channel = channel;
			this.message = message;
		}
	}
}

/*
:ohnx-!~ohnx@1.2.3.4 JOIN #/
:hobana.freenode.net 332 ohnx- #/ :roots here.
:hobana.freenode.net 333 ohnx- #/ unknown 1212089057
:hobana.freenode.net 353 ohnx- = #/ :ohnx- ohnx
:hobana.freenode.net 366 ohnx- #/ :End of /NAMES list.

:
*/
