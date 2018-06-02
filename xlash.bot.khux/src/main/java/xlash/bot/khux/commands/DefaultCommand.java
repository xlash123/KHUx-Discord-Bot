package xlash.bot.khux.commands;

import org.javacord.api.entity.message.Message;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.config.ServerConfig;

/**
 * Changes the default game. Affects !medal, !lux, and !uc
 *
 */
public class DefaultCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!default"};
	}
	
	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		ServerConfig config = this.getServerConfig(message);
		if(args[0].equalsIgnoreCase("get")){
			message.getChannel().sendMessage("Default game is currently " + config.defaultGame.toString());
			return;
		}
		config.defaultGame = GameEnum.parseString(args[0]);
		message.getChannel().sendMessage("Default game changed to " + config.defaultGame.toString());
	}

	@Override
	public String getDescription() {
		return "Changes the default game. Affects !medal, !lux, and !uc";
	}

	@Override
	public String getUsage() {
		return "!default [na/jp/get]";
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

}
