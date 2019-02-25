package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.Optional;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.TwitterHandler.Tweet;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.sheduler.TimedEvent;

/**
 * Automatically posts Tweets to a given channel
 *
 */
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
				config.updateChannelNA = message.getChannel().getIdAsString();
			}else {
				config.updateChannelJP = message.getChannel().getIdAsString();
			}
    		message.getChannel().sendMessage(game.toString() + " Twitter updates are set to post on this channel.");
			break;
		case "off":
			if(game==GameEnum.NA) {
				config.updateChannelNA = "";
			}else {
				config.updateChannelJP = "";
			}
    		message.getChannel().sendMessage(game.toString() + " Twitter updates have been turned off.");
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
				message.getChannel().sendMessage(KHUxBot.twitterHandler.getTwitterUpdateLink(0, game).getLink());
			}
			break;
		case "status":
			Optional<TextChannel> naChannel = KHUxBot.api.getTextChannelById(config.updateChannelNA);
			Optional<TextChannel> jpChannel = KHUxBot.api.getTextChannelById(config.updateChannelJP);
			if(naChannel.isPresent()) {
				message.getChannel().sendMessage("NA Twitter update reminders are set for channel: <#" + naChannel.get().getIdAsString() + ">");
			}else message.getChannel().sendMessage("NA Twitter updates are currently turned off.");
			if(jpChannel.isPresent()) {
				message.getChannel().sendMessage("JP Twitter update reminders are set for channel: <#" + jpChannel.get().getIdAsString() + ">");
			}else message.getChannel().sendMessage("JP Twitter updates are currently turned off.");
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
