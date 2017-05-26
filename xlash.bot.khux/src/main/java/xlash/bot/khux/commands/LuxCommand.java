package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

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
		switch(args[0]){
		case "on":
			message.reply("Double lux reminders have been turned on.");
			KHUxBot.config.luxChannel = message.getChannelReceiver().getId();
			KHUxBot.shouldLux = true;
			break;
		case "off":
			message.reply("Double lux reminders have been turned off.");
			KHUxBot.config.luxChannel = "";
			KHUxBot.shouldLux = false;
    		break;
		case "status":
			if (KHUxBot.shouldLux)
				message.reply("Double lux reminders are set for channel: #"
						+ KHUxBot.api.getChannelById(KHUxBot.config.luxChannel).getName());
			else
				message.reply("Double lux reminders are currently turned off.");
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
		return "!lux [on/off/status]";
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

}
