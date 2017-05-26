package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

public class ResetCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!reset"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		message.reply("Resetting medal descriptions. Please wait...");
		KHUxBot.medalHandler.resetDescriptions();
		message.reply("Done! You may continue to query me.");
	}

	@Override
	public String getDescription() {
		return "Resets all cached medal descriptions for both games.";
	}

	@Override
	public String getUsage() {
		return "!reset";
	}
	
	@Override
	public boolean isAdmin(){
		return true;
	}

}
