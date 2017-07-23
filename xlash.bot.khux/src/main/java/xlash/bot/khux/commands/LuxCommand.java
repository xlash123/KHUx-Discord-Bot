package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;

public class LuxCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!lux"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		for(int i=0; i<args.length; i++){
			args[i] = args[i].toLowerCase();
		}
		ServerConfig config = this.getServerConfig(message);
		GameEnum game = config.defaultGame;
		if(args.length > 1){
			game = GameEnum.parseString(args[1]);
		}
		switch(args[0]){
		case "on":
			if(game==GameEnum.NA){
				if(!config.luxChannelNA.isEmpty()){
					message.reply("NA Lux reminders are already on.");
					return;
				}
				config.luxChannelNA = message.getChannelReceiver().getId();
			}else{
				if(!config.luxChannelJP.isEmpty()){
					message.reply("JP Lux reminders are already on.");
					return;
				}
				config.luxChannelJP = message.getChannelReceiver().getId();
			}
			message.reply("Double lux reminders for " + game + " have been turned on.");
			break;
		case "off":
			if(game==GameEnum.NA){
				if(config.luxChannelNA.isEmpty()){
					message.reply("NA Lux reminders are already off.");
					return;
				}
				config.luxChannelNA = "";
			}else{
				if(!config.luxChannelJP.isEmpty()){
					message.reply("JP Lux reminders are already off.");
					return;
				}
				config.luxChannelJP = "";
			}
			message.reply("Double lux reminders for " + game + " have been turned off.");
    		break;
		case "status":
			if (!config.luxChannelNA.isEmpty())
				message.reply("Double lux reminders for NA are set for channel: #"
						+ KHUxBot.api.getChannelById(config.luxChannelNA).getName());
			else
				message.reply("Double lux reminders for NA are currently turned off.");
			if (!config.luxChannelJP.isEmpty())
				message.reply("Double lux reminders for JP are set for channel: #"
						+ KHUxBot.api.getChannelById(config.luxChannelJP).getName());
			else
				message.reply("Double lux reminders for JP are currently turned off.");
			return;
			default:
				this.printDescriptionUsage(message);
				return;
		}
		config.saveConfig();
	}

	@Override
	public String getDescription() {
		return "Alerts when double lux time is active to a given channel.";
	}

	@Override
	public String getUsage() {
		return "!lux [on/off/status] (na/jp)";
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

}
