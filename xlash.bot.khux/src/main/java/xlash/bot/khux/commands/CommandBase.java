package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;

public abstract class CommandBase{
	
	public abstract String[] getAliases();
	
	public abstract void onCommand(String[] args, Message message);
	
	public abstract String getDescription();
	
	public abstract String getUsage();
	
	public boolean isAdmin(){
		return false;
	}
	
	public final ServerConfig getServerConfig(Message message){
		return KHUxBot.getServerConfig(message.getChannelReceiver().getServer());
	}
	
	public void printDescriptionUsage(Message message){
		message.reply(this.getDescription() + "\n" + this.getUsage());
	}

}
