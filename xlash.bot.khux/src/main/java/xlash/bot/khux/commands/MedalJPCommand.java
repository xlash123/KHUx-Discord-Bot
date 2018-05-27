package xlash.bot.khux.commands;

import java.awt.Color;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.medals.Medal;
import xlash.bot.khux.medals.SearchQuery;

/**
 * Gets the info on a medal from the JP version
 *
 */
public class MedalJPCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!medaljp"};
	}
	
	public void onCommand(String[] args, Message message){
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		String medalName = "";
		for(int i=0; i<args.length; i++){
			medalName += args[i] + " ";
		}
		GameEnum game = GameEnum.JP;
		medalName = medalName.trim();
		EmbedBuilder eb = new EmbedBuilder();
		SearchQuery query = KHUxBot.medalHandler.searchMedalByName(medalName, game);
		if(query.queries.size()==0) {
			eb.setColor(Color.RED);
			eb.setDescription("I could not find any medals with that name.");
			message.getChannel().sendMessage("", eb);
		}else if(query.queries.size()==1) {
			Medal medal = KHUxBot.medalHandler.getMedalByMid(query.queries.get(0).mid, game);
			message.getChannel().sendMessage("", KHUxBot.medalHandler.prepareMedalMessage(medal, false));
		}else {
			KHUxBot.medalHandler.promptQuery(query, message, game);
		}
	}

	@Override
	public String getDescription() {
		return "Gets the info on a medal from the JP version.";
	}

	@Override
	public String getUsage() {
		return "!medaljp [name]";
	}

}
