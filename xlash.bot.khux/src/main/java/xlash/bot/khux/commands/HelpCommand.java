package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

public class HelpCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!help", "!?", "!commands"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		String toSend = "```";
		for(CommandBase com : KHUxBot.commandHandler.commands) {
			toSend += com.getAliases()[0] + ": " + com.getDescription() + "\n Usage: " + com.getUsage() + "\n";
			if(com.getAliases().length > 1) {
				toSend += " Aliases:";
				for(int i=1; i<com.getAliases().length; i++) {
					toSend += " " + com.getAliases()[i];
				}
				toSend += "\n";
			}
		}
		toSend += "```";
		message.getAuthor().sendMessage(toSend);
	}

	@Override
	public String getDescription() {
		return "Sends the user the list of commands with usage";
	}

	@Override
	public String getUsage() {
		return "!help";
	}

}
