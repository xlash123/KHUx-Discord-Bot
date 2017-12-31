package xlash.bot.khux.commands;

import java.util.ArrayList;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.TwitterHandler.Tweet;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.sheduler.TimedEvent;

public class TweetCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!tweet"};
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
		if(args.length > 1) {
			game = GameEnum.parseString(args[1]);
		}
		switch(args[0]){
		case "on":
			if(game==GameEnum.NA) {
				config.updateChannelNA = message.getChannelReceiver().getId();
			}else {
				config.updateChannelJP = message.getChannelReceiver().getId();
			}
    		message.reply(game.toString() + " Twitter updates are set to post on this channel.");
			break;
		case "off":
			if(game==GameEnum.NA) {
				config.updateChannelNA = "";
			}else {
				config.updateChannelJP = "";
			}
    		message.reply(game.toString() + " Twitter updates have been turned off.");
    		break;
		case "get":
			ArrayList<Tweet> toSend = KHUxBot.twitterHandler.getNewTwitterLinks(game, false);
			if(!toSend.isEmpty()){
				TimedEvent update = KHUxBot.scheduler.getTimedEvent("Twitter Update " + game.toString());
				if(update != null){
					update.run();
				}else{
					System.err.println(game.toString() + " Twitter Update doesn't exist");
				}
			}else{
				message.getChannelReceiver().sendMessage(KHUxBot.twitterHandler.getTwitterUpdateLink(0, game).getLink());
			}
			break;
		case "status":
			if(!config.updateChannelNA.isEmpty()) {
				message.reply("NA Twitter update reminders are set for channel: #" + KHUxBot.api.getChannelById(config.updateChannelNA).getName());
			}else message.reply("NA Twitter updates are currently turned off.");
			if(!config.updateChannelJP.isEmpty()) {
				message.reply("JP Twitter update reminders are set for channel: #" + KHUxBot.api.getChannelById(config.updateChannelJP).getName());
			}else message.reply("JP Twitter updates are currently turned off.");
			return;
			default:
				this.printDescriptionUsage(message);
				return;
		}
		config.saveConfig();
	}

	@Override
	public String getDescription() {
		return "Automatically posts Tweets to a given channel.";
	}

	@Override
	public String getUsage() {
		return "!tweet [on/off/get/status] (NA/JP)";
	}
	
	@Override
	public boolean isAdmin(){
		return true;
	}

}
