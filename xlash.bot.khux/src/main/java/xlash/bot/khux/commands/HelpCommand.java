package xlash.bot.khux.commands;

import java.awt.Color;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.KHUxBot;

/**
 * Sends the user the list of commands with usage
 *
 */
public class HelpCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!help", "!?", "!commands"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.CYAN);
		for(CommandBase com : KHUxBot.commandHandler.commands) {
			eb.setTitle("List of all commands:");
			eb.addField(com.getAliases()[0], "**Description:** " + com.getDescription() + "\n**Aliases:** " + com.getAliasesTogether() + "\n**Usage:** " + com.getUsage(), false);
		}
		message.getAuthor().sendMessage("", eb);
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
