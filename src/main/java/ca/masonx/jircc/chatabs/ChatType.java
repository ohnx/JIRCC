/**
 * ChatType enum used to convey what kind of chat message this is
 * 
 * Converting a ChatMessage toString relies upon this enum to determine
 * the output format.
 */
package ca.masonx.jircc.chatabs;

public enum ChatType {
	/* Regular chat message */
	CHAT_MESSAGE,
	/* Action chat message */
	ACTION_MESSAGE,
	/* CTCP version request */
	VERSION_MESSAGE,
	/* User joined channel */
	JOIN_MESSAGE,
	/* User parted channel */
	PART_MESSAGE,
	/* User quits channel */
	QUIT_MESSAGE,
	/* User kicked from channel */
	KICK_MESSAGE,
	/* User changed mode in a channel */
	MODE_MESSAGE,
	/* User changed nick */
	NICK_MESSAGE,
	/* Topic of a channel */
	TOPIC_MESSAGE,
	/* Unknown message */
	UNKNOWN_MESSAGE
}
