package xlash.bot.khux.commands;

import java.awt.Color;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.medals.SearchQuery;

/**
 * Gets the info on a medal of the default game
 *
 */
public class MedalCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!medal"};
	}
	
	public void onCommand(String[] args, Message message){
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		ServerConfig config = this.getServerConfig(message);
		String medalName = "";
		for(int i=0; i<args.length; i++){
			medalName += args[i] + " ";
		}
		GameEnum game = config.defaultGame;
		medalName = medalName.trim();
		EmbedBuilder eb = new EmbedBuilder();
		SearchQuery query = KHUxBot.medalHandler.searchMedalByName(medalName, game);
		if(query.queries.size()==0) {
			eb.setColor(Color.RED);
			eb.setDescription("I could not find any medals with that name.");
			message.getChannel().sendMessage("", eb);
		}else if(query.queries.size()==1) {
			KHUxBot.medalHandler.createMedalMessage(KHUxBot.medalHandler.getMedalByMid(query.queries.get(0).mid, game), message);
		}else {
			KHUxBot.medalHandler.promptQuery(query, message, game);
		}
	}

	@Override
	public String getDescription() {
		return "Gets the info on a medal of the default game.";
	}

	@Override
	public String getUsage() {
		return "!medal [name]";
	}

}
