package xlash.bot.khux;

import java.util.Date;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.Reaction;

/**
 * A message that has an action attached to it. Used for responding to reaction emoji messages.
 *
 */
public abstract class ActionMessage {
	
	public Message messageStored;
	public boolean dead;
	
	public ActionMessage(Message message) {
		this.messageStored = message;
	}

	/**
	 * Runs when the action message is called
	 * @param reaction
	 */
	public abstract void run(Reaction reaction);
	
	/**
	 * Determines if the message is expired and should be deleted (1 hour)
	 * @return if the message is expired
	 */
	public boolean isExpired() {
		return new Date().getTime()-messageStored.getCreationDate().getTimeInMillis()>=3600000;
	}
	
	/**
	 * Returns if the message is the same as a the paramter
	 * @param message the message to compare
	 * @return if the messages are the same
	 */
	public boolean isSameMessage(Message message) {
		return this.messageStored.getId().equals(message.getId());
	}
	
	/**
	 * Sets the message to die
	 */
	public void kill() {
		this.dead = true;
	}
	
}
