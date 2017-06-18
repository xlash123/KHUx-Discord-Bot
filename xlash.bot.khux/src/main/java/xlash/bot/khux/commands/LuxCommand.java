package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.Config;

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
		GameEnum game = KHUxBot.config.defaultGame;
		if(args.length > 1){
			game = GameEnum.parseString(args[1]);
		}
		switch(args[0]){
		case "on":
			if(game==GameEnum.NA){
				KHUxBot.config.luxChannelNA = message.getChannelReceiver().getId();
				KHUxBot.shouldLuxNA = true;
			}else{
				KHUxBot.config.luxChannelJP = message.getChannelReceiver().getId();
				KHUxBot.shouldLuxJP = true;
			}
			KHUxBot.scheduler.enableEvent(game + " Lux On");
			KHUxBot.scheduler.enableEvent(game + " Lux Off");
			message.reply("Double lux reminders for " + game + " have been turned on.");
			break;
		case "off":
			if(game==GameEnum.NA){
				KHUxBot.config.luxChannelNA = "";
				KHUxBot.shouldLuxNA = false;
			}else{
				KHUxBot.config.luxChannelJP = "";
				KHUxBot.shouldLuxJP = false;
			}
			KHUxBot.scheduler.disableEvent(game + " Lux On");
			KHUxBot.scheduler.disableEvent(game + " Lux Off");
			message.reply("Double lux reminders for " + game + " have been turned off.");
    		break;
		case "status":
			if (KHUxBot.shouldLuxNA)
				message.reply("Double lux reminders for NA are set for channel: #"
						+ KHUxBot.api.getChannelById(KHUxBot.config.luxChannelNA).getName());
			else
				message.reply("Double lux reminders for NA are currently turned off.");
			if (KHUxBot.shouldLuxJP)
				message.reply("Double lux reminders for JP are set for channel: #"
						+ KHUxBot.api.getChannelById(KHUxBot.config.luxChannelJP).getName());
			else
				message.reply("Double lux reminders for JP are currently turned off.");
			break;
			default:
				this.printDescriptionUsage(message);
		}
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
