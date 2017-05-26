package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

public class MedalJPCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!medaljp"};
	}
	
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
		if(realName==null){
			message.reply("I don't know what medal that is.");
			return;
		}
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
