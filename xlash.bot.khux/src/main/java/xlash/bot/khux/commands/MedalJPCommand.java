package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

public class MedalJPCommand extends CommandBase{
	
	@Command(aliases="!medaljp")
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
		String realName = KHUxBot.medalHandler.getRealNameByNickname(medalName, GameEnum.JP);
		message.reply(KHUxBot.medalHandler.getMedalInfo(realName, GameEnum.JP));
	}

	@Override
	public String getDescription() {
		return "Gets the info on a medal from the JP version.";
	}

	@Override
	public String getUsage() {
		return "!medaljp [name]";
	}

}
