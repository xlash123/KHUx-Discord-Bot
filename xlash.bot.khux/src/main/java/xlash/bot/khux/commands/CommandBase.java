package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;

public abstract class CommandBase{
	
	public abstract String[] getAliases();
	
	public abstract void onCommand(String[] args, Message message);
	
	public abstract String getDescription();
	
	public abstract String getUsage();
	
	public boolean isAdmin(){
		return false;
	}
	
	public void printDescriptionUsage(Message message){
		message.reply(this.getUsage() + "\n" + this.getDescription());
	}

}
