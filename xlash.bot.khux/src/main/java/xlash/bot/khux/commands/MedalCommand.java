package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;

public class MedalCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!medal"};
	}
	
	public void onCommand(String[] args, Message message){
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		while(KHUxBot.medalHandler.isDisabled()){}
		ServerConfig config = this.getServerConfig(message);
		String medalName = "";
		for(int i=0; i<args.length; i++){
			medalName += args[i] + " ";
		}
		medalName = medalName.trim();
		String realName = KHUxBot.medalHandler.getRealNameByNickname(medalName, config.defaultGame);
		if(realName==null){
			message.reply("I don't know what medal that is.");
			return;
		}
		message.reply(KHUxBot.medalHandler.getMedalInfo(realName, config.defaultGame));
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
