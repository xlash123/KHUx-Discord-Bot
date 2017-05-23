package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.sdcf4j.Command;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

public class DefaultCommand extends CommandBase{

	@Override
	@Command(aliases="!default")
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		if(args[0].equalsIgnoreCase("get")){
			message.reply("Default game is currently " + KHUxBot.config.defaultGame.toString());
			return;
		}
		KHUxBot.config.defaultGame = GameEnum.parseString(args[0]);
		message.reply("Default game changed to " + KHUxBot.config.defaultGame.toString());
	}

	@Override
	public String getDescription() {
		return "Changes the default game. Affects !medal and !lux";
	}

	@Override
	public String getUsage() {
		return "!default [na/jp/get]";
	}

}
