package xlash.bot.khux.commands;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

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
		aliases = aliases.substring(0, aliases.length()-2);
		System.out.println(aliases);
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
	 * Returns if this command should only be used for servers and not DMs
	 * @return true if the command should only be used for server
	 */
	public boolean isServerOnly() {
		return false;
	}
	
	/**
	 * Returns the server config of the server from which the message was sent
	 * @param message the user's message
	 * @return server config
	 */
	public final ServerConfig getServerConfig(Message message){
		if(message.getServer().isPresent()) {
			return KHUxBot.getServerConfig(message.getServer().get());
		}else return KHUxBot.getServerConfig(message.getUserAuthor().get());
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
		message.getChannel().sendMessage("", eb);
	}
	
	public void reply(Message message, String content) {
		message.getChannel().sendMessage(content);
	}
	
	public void reply(Message message, EmbedBuilder eb) {
		message.getChannel().sendMessage(eb);
	}

}
