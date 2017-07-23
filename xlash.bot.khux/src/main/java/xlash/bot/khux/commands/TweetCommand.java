package xlash.bot.khux.commands;

import java.util.ArrayList;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;
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
		switch(args[0]){
		case "on":
    		config.updateChannel = message.getChannelReceiver().getId();
    		message.reply("Twitter updates are set to post on this channel.");
			break;
		case "off":
    		config.updateChannel = "";
    		message.reply("Twitter updates have been turned off.");
    		break;
		case "get":
			ArrayList<String> toSend = KHUxBot.twitterHandler.getNewTwitterLinks();
			if(!toSend.isEmpty()){
				TimedEvent update = KHUxBot.scheduler.getTimedEvent("Twitter Update");
				if(update != null){
					update.run();
				}else{
					System.err.println("Twitter Update doesn't exist");
				}
			}else{
				message.getChannelReceiver().sendMessage(KHUxBot.twitterHandler.getTwitterUpdateLink(0));
			}
			break;
		case "status":
			if(!config.updateChannel.isEmpty())message.reply("Twitter update reminders are set for channel: #" + KHUxBot.api.getChannelById(config.updateChannel).getName());
    		else message.reply("Twitter updates are currently turned off.");
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
		return "!tweet [on/off/get/status]";
	}
	
	@Override
	public boolean isAdmin(){
		return true;
	}

}
