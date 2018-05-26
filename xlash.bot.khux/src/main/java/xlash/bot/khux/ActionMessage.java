package xlash.bot.khux;

import java.util.Date;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.Reaction;

/**
 * A message that has an action attached to it. Used for responding to reaction emoji messages.
 *
 */
public abstract class ActionMessage {
	
	/**
	 * The id of the message to which this pertains. Call using the api.
	 */
	public String messageId;
	public boolean dead;
	
	/**
	 * Runs the run method on the specified message when any reaction emoji surpasses 1 and the optional test method returns true.
	 * @param message
	 */
	public ActionMessage(Message message) {
		this.messageId = message.getId();
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
		return new Date().getTime()-KHUxBot.api.getMessageById(messageId).getCreationDate().getTimeInMillis()>=3600000;
	}
	
	/**
	 * Returns if the message is the same as a the paramter
	 * @param message the message to compare
	 * @return if the messages are the same
	 */
	public boolean isSameMessage(Message message) {
		return this.messageId.equals(message.getId());
	}
	
	/**
	 * Override to conditionally run this action
	 * @return should the action run
	 */
	public boolean test() {
		return true;
	}
	
	/**
	 * Sets the message to die
	 */
	public void kill() {
		this.dead = true;
	}
	
}
