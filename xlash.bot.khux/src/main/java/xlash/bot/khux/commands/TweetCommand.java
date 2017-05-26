package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

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
		switch(args[0]){
		case "on":
			message.reply("Twitter updates are set to post on this channel.");
    		KHUxBot.config.updateChannel = message.getChannelReceiver().getId();
    		KHUxBot.shouldTwitterUpdate = true;
			break;
		case "off":
			message.reply("Twitter updates have been turned off.");
    		KHUxBot.config.updateChannel = "";
    		KHUxBot.shouldTwitterUpdate = false;
    		break;
		case "get":
			message.reply(KHUxBot.twitterHandler.getTwitterUpdateLink(0));
			break;
		case "status":
			if(KHUxBot.shouldTwitterUpdate)message.reply("Twitter update reminders are set for channel: #" + KHUxBot.api.getChannelById(KHUxBot.config.updateChannel).getName());
    		else message.reply("Twitter updates are currently turned off.");
			break;
			default:
				this.printDescriptionUsage(message);
		}
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
