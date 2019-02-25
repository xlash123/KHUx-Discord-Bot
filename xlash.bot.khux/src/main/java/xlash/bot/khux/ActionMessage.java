package xlash.bot.khux;

import java.util.Date;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;

/**
 * A message that has an action attached to it. Used for responding to reaction emoji messages.
 *
 */
public abstract class ActionMessage {
	
	/**
	 * The id of the message to which this pertains. Call using the api.
	 */
	public String messageId;
	public TextChannel channel;
	public boolean killable;
	public boolean dead;
	
	public boolean unlocked, supernova;
	
	/**
	 * Runs the run method on the specified message when any reaction emoji surpasses 1 and the optional test method returns true.
	 * @param message
	 */
	public ActionMessage(Message message) {
		this(message, true);
	}
	
	public ActionMessage(Message message, boolean killable) {
		this.messageId = message.getIdAsString();
		channel = message.getChannel();
		this.killable = killable;
	}

	/**
	 * Runs when the action message is called
	 * @param reaction
	 */
	public abstract void run(Reaction reaction, Type type);
	
	/**
	 * Determines if the message is expired and should be deleted (24 hours)
	 * @return if the message is expired
	 */
	public boolean isExpired() {
		long timeDiff = new Date().getTime() - KHUxBot.api.getMessageById(messageId, channel).join().getCreationTimestamp().toEpochMilli();
		return timeDiff >= (24*60*60*1000); //24 hours
	}
	
	/**
	 * Returns if the message is the same as a the paramter
	 * @param message the message to compare
	 * @return if the messages are the same
	 */
	public boolean isSameMessage(Message message) {
		return this.messageId.equals(message.getIdAsString());
	}
	
	/**
	 * Override to conditionally run this action. By default, this will return true when a reaction is added.
	 * @return should the action run
	 */
	public boolean test(Type type) {
		return type==Type.ADD;
	}
	
	/**
	 * Sets the message to die
	 */
	public void kill() {
		this.dead = true;
	}
	
	public enum Type {
		
		ADD, REMOVE
		
	}
	
}
