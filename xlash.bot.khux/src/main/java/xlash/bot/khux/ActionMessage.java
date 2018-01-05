package xlash.bot.khux;

import java.util.Date;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.Reaction;

public abstract class ActionMessage {
	
	public Message message;
	public boolean dead;
	
	public ActionMessage(Message message) {
		this.message = message;
	}

	public abstract void run(Reaction reaction);
	
	public boolean isExpired() {
		return new Date().getTime()-message.getCreationDate().getTimeInMillis()>=3600000;
	}
	
	public boolean isSameMessage(Message message) {
		return this.message.getId().equals(message.getId());
	}
	
	public void kill() {
		this.dead = true;
	}
	
}
