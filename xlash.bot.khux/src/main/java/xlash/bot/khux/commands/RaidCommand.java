package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.BonusTimes;

public class RaidCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[] {"!raid", "!dailyraid", "!raidevent"};
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
				if(!config.raidChannelNA.isEmpty()){
					message.reply("NA raid reminders are already on.");
					return;
				}
				config.raidChannelNA = message.getChannelReceiver().getId();
			}else{
				if(!config.raidChannelJP.isEmpty()){
					message.reply("JP raid reminders are already on.");
					return;
				}
				config.raidChannelJP = message.getChannelReceiver().getId();
			}
			message.reply("Raid reminders for " + game + " have been turned on.");
			break;
		case "off":
			if(game==GameEnum.NA){
				if(config.raidChannelNA.isEmpty()){
					message.reply("NA raid reminders are already off.");
					return;
				}
				config.raidChannelNA = "";
			}else{
				if(!config.raidChannelJP.isEmpty()){
					message.reply("JP raid reminders are already off.");
					return;
				}
				config.raidChannelJP = "";
			}
			message.reply("Raid reminders for " + game + " have been turned off.");
    		break;
		case "status":
			if (!config.raidChannelNA.isEmpty())
				message.reply("Raid reminders for NA are set for channel: #"
						+ KHUxBot.api.getChannelById(config.raidChannelNA).getName());
			else
				message.reply("Raid reminders for NA are currently turned off.");
			if (!config.raidChannelJP.isEmpty())
				message.reply("Raid reminders for JP are set for channel: #"
						+ KHUxBot.api.getChannelById(config.raidChannelJP).getName());
			else
				message.reply("Raid reminders for JP are currently turned off.");
			return;
		case "check":
			int nextTime = BonusTimes.raidTimeDifference(game);
			int mins = nextTime%60;
			int hours = nextTime/60;
			String timeS = "";
			if(hours > 0) {
				timeS += hours + " hours ";
				if(mins > 0) timeS += "and ";
			}
			if(mins > 0) timeS += mins + " minutes ";
			if(timeS.isEmpty()) {
				message.reply("The daily raid boss for " + game.name() + " just appeared!");
			}else message.reply("There are " + timeS + " until the daily raid boss appears for " + game.name() + ".");
			break;
		case "remind":
			if(args.length > 1) {
				try {
					int time = Integer.parseInt(args[1]);
					if(time > 30 || time < 0) {
						message.reply("Out of range. Enter a number 0-30 inclusive.");
					}else {
						message.reply("Raid reminder set for " + time + " minutes before active time.");
						config.raidRemind = time;
					}
				}catch(NumberFormatException e) {
					message.reply("I don't think that's a number... Enter a number 0-30 inclusive.");
				}
			}else {
				this.printDescriptionUsage(message);
				return;
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
		return "Reminds the server of when a daily raid boss is able to be attacked.";
	}

	@Override
	public String getUsage() {
		return "!raid [on/off/status/check] (na/jp) or !raid remind [minutes]";
	}

}
