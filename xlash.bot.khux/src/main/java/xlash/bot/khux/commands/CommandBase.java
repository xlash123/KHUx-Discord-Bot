package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;

/**
 * The base for all commands
 *
 */
public abstract class CommandBase{
	
	/**
	 * Gets a list of all names that calls this command
	 * @return String array
	 */
	public abstract String[] getAliases();
	
	public String getAliasesTogether() {
		String aliases = "";
		for(String s : this.getAliases()) {
			aliases += s + ", ";
		}
		return aliases;
	}
	
	/**
	 * Runs when the command is called
	 * @param args parameters
	 * @param message message that was sent by the user
	 */
	public abstract void onCommand(String[] args, Message message);
	
	/**
	 * Description of what the command does
	 * @return description
	 */
	public abstract String getDescription();
	
	/**
	 * Description of how to properly call the command
	 * @return usage description
	 */
	public abstract String getUsage();
	
	/**
	 * Returns if this command be run by only admins
	 * @return true if an admin command
	 */
	public boolean isAdmin(){
		return false;
	}
	
	/**
	 * Returns the server config of the server from which the message was sent
	 * @param message the user's message
	 * @return server config
	 */
	public final ServerConfig getServerConfig(Message message){
		if(message.getChannelReceiver() != null) {
			return KHUxBot.getServerConfig(message.getChannelReceiver().getServer());
		}else return ServerConfig.getBlank();
	}
	
	/**
	 * Combines the description of a command plus its usage and sends it to the user who sent the message
	 * @param message message sent by the user who will receive this message
	 */
	public void printDescriptionUsage(Message message){
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(this.getAliases()[0]);
		eb.addField("Description", this.getDescription(), false);
		eb.addField("Aliases", this.getAliasesTogether(), false);
		eb.addField("Usage", this.getUsage(), false);
		message.reply("", eb);
	}

}
