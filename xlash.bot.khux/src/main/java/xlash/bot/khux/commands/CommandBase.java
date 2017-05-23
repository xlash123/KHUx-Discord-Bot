package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;

public abstract class CommandBase implements CommandExecutor{
	
	public abstract void onCommand(String[] args, Message message);
	
	public abstract String getDescription();
	
	public abstract String getUsage();
	
	public void printDescriptionUsage(Message message){
		message.reply(this.getUsage() + "\n" + this.getDescription());
	}

}
