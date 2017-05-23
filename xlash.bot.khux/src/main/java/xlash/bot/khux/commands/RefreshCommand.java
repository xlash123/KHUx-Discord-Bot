package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import xlash.bot.khux.KHUxBot;

public class RefreshCommand extends CommandBase{

	@Override
	@Command(aliases="!refresh")
	public void onCommand(String[] args, Message message) {
		message.reply("Refreshing medal list. Please wait...");
		KHUxBot.medalHandler.refreshMedalList();
		message.reply("Done! You may continue to query me.");
	}

	@Override
	public String getDescription() {
		return "Refreshes the list of registered medals in both games.";
	}

	@Override
	public String getUsage() {
		return "!refresh";
	}

}
