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
		if(args.length==0){
			this.printDescriptionUsage(message);
		}else{
			switch(args[0].toLowerCase()){
			case "load":
				KHUxBot.config.loadConfig();
				message.reply("Configuration file reloaded.");
				break;
			case "save":
				KHUxBot.config.saveConfig();
				message.reply("Configuration file saved.");
				break;
				default:
					this.printDescriptionUsage(message);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Loads the config file.";
	}

	@Override
	public String getUsage() {
		return "!config [save/load]";
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}

}
