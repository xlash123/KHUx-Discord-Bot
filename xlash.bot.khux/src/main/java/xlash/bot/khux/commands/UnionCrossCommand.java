package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.BonusTimes;

public class UnionCrossCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[] {"!uc", "!ux", "!unioncross"};
	}

	//I like UX, but community likes UC, so whatever
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
				if(!config.uxChannelNA.isEmpty()){
					message.reply("NA UC reminders are already on.");
					return;
				}
				config.uxChannelNA = message.getChannelReceiver().getId();
			}else{
				if(!config.uxChannelJP.isEmpty()){
					message.reply("JP UC reminders are already on.");
					return;
				}
				config.uxChannelJP = message.getChannelReceiver().getId();
			}
			message.reply("UC reminders for " + game + " have been turned on.");
			break;
		case "off":
			if(game==GameEnum.NA){
				if(config.uxChannelNA.isEmpty()){
					message.reply("NA UC reminders are already off.");
					return;
				}
				config.uxChannelNA = "";
			}else{
				if(!config.uxChannelJP.isEmpty()){
					message.reply("JP UC reminders are already off.");
					return;
				}
				config.uxChannelJP = "";
			}
			message.reply("UC reminders for " + game + " have been turned off.");
    		break;
		case "status":
			if (!config.uxChannelNA.isEmpty())
				message.reply("UC reminders for NA are set for channel: #"
						+ KHUxBot.api.getChannelById(config.uxChannelNA).getName());
			else
				message.reply("UC reminders for NA are currently turned off.");
			if (!config.uxChannelJP.isEmpty())
				message.reply("UC reminders for JP are set for channel: #"
						+ KHUxBot.api.getChannelById(config.uxChannelJP).getName());
			else
				message.reply("UC reminders for JP are currently turned off.");
			return;
		case "check":
			int nextTime = BonusTimes.luxTimeDifference(game);
			int mins = nextTime%60;
			int hours = nextTime/60;
			String timeS = "";
			if(hours > 0) {
				timeS += hours + " hours ";
				if(mins > 0) timeS += "and ";
			}
			if(mins > 0) timeS += mins + " minutes ";
			if(timeS.isEmpty()) {
				message.reply("UC bonus time for " + game.name() + " just went active!");
			}else message.reply("There are " + timeS + "until UC bonus time is active for " + game.name() + ".");
			break;
		case "remind":
			if(args.length > 1) {
				try {
					int time = Integer.parseInt(args[1]);
					if(time > 30 || time < 0) {
						message.reply("Out of range. Enter a number 0-30 inclusive.");
					}else {
						message.reply("UC reminder set for " + time + " minutes before bonus time.");
						config.uxRemind = time;
					}
				}catch(NumberFormatException e) {
					message.reply("I don't think that's a number... Enter a number 0-30 inclusive.");
				}
			}else {
				this.printDescriptionUsage(message);
			}
			break;
			default:
				this.printDescriptionUsage(message);
				return;
		}
		config.saveConfig();
	}

	@Override
	public String getDescription() {
		return "Reminds the server when the Union Cross bonus times are active.";
	}

	@Override
	public String getUsage() {
		return "!ux [on/off/status/check] (na/jp) or !ux remind [minutes]";
	}

}
