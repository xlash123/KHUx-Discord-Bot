package xlash.bot.khux.commands;

import org.javacord.api.entity.message.Message;

import xlash.bot.khux.KHUxBot;

/**
 * Adds to a global counter of the amount of saltiness in the community
 *
 */
public class SaltCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[] {"!salt", "!rip", "!wtf"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		message.getChannel().sendMessage("Salt levels have increased to " + (++KHUxBot.botConfig.saltCount) + ".");
		KHUxBot.botConfig.saveConfig();
	}

	@Override
	public String getDescription() {
		return "Adds to a global counter of the amount of saltiness in the community.";
	}

	@Override
	public String getUsage() {
		return "!salt or !rip or !wtf";
	}

}
