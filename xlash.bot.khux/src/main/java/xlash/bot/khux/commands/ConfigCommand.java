package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

public class ConfigCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!config"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		KHUxBot.config.loadConfig();
		message.reply("Configuration file reloaded.");
	}

	@Override
	public String getDescription() {
		return "Loads the config file.";
	}

	@Override
	public String getUsage() {
		return "!config";
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}

}
