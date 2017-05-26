package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import xlash.bot.khux.KHUxBot;

public class MedalCommand extends CommandBase{
	
	@Command(aliases="!medal")
	public void onCommand(String[] args, Message message){
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		while(KHUxBot.medalHandler.isDisabled()){}
		String medalName = "";
		for(int i=0; i<args.length; i++){
			medalName += args[i] + " ";
		}
		medalName = medalName.trim();
		String realName = KHUxBot.medalHandler.getRealNameByNickname(medalName, KHUxBot.config.defaultGame);
		if(realName==null){
			message.reply("I don't know what medal that is.");
		}
		message.reply(KHUxBot.medalHandler.getMedalInfo(realName, KHUxBot.config.defaultGame));
	}

	@Override
	public String getDescription() {
		return "Gets the info on a medal of the default game.";
	}

	@Override
	public String getUsage() {
		return "!medal [name]";
	}

}
