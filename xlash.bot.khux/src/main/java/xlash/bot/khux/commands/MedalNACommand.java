package xlash.bot.khux.commands;

import java.awt.Color;
import java.util.concurrent.ExecutionException;

import com.vdurmont.emoji.EmojiManager;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.Reaction;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.ActionMessage;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.medals.Medal;
import xlash.bot.khux.medals.SearchQuery;

public class MedalNACommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!medalna"};
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
		GameEnum game = GameEnum.NA;
		medalName = medalName.trim();
		EmbedBuilder eb = new EmbedBuilder();
		SearchQuery query = KHUxBot.medalHandler.searchMedalByName(medalName, game);
		if(query.queries.size()==0) {
			eb.setColor(Color.RED);
			eb.setDescription("I could not find any medals with that name.");
			message.reply("", eb);
		}else if(query.queries.size()==1) {
			Medal medal = KHUxBot.medalHandler.getMedalByMid(query.queries.get(0).mid, game);
			message.reply("", KHUxBot.medalHandler.prepareMedalMessage(medal));
		}else {
			String one = EmojiManager.getForAlias("one").getUnicode();
			String two = EmojiManager.getForAlias("two").getUnicode();
			String three = EmojiManager.getForAlias("three").getUnicode();
			eb.setColor(Color.YELLOW);
			eb.setTitle("Did you mean...");
			boolean flag = query.queries.size() > 2;
			eb.addField(one, query.queries.get(0).name, true);
			eb.addField(two, query.queries.get(1).name, true);
			if(flag) eb.addField(three, query.queries.get(2).name, true);
			eb.setFooter("Click or tap on the reaction that corresponds with the medal you want.");
			try {
				Message futureMessage = message.reply("", eb).get();
				KHUxBot.actionMessages.add(new ActionMessage(futureMessage) {
					@Override
					public void run(Reaction reaction) {
						Channel channel = futureMessage.getChannelReceiver();
						futureMessage.delete();
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						String unicode = reaction.getUnicodeEmoji();
						String choice = "";
						if(unicode.equals(one)) {
							choice += query.queries.get(0).mid;
						}else if(unicode.equals(two)){
							choice += query.queries.get(1).mid;
						}else if(flag){
							choice += query.queries.get(2).mid;
						}
						Medal medal = KHUxBot.medalHandler.getMedalByMid(choice, game);
						channel.sendMessage("", KHUxBot.medalHandler.prepareMedalMessage(medal));
					}
				});
				futureMessage.addUnicodeReaction(one);
				//It needs to wait for the reaction to actually be added. I've tried using Future.isDone(), but that doesn't seem to work.
				//Could potentially break if speed is slow
				Thread.sleep(350);
				futureMessage.addUnicodeReaction(two);
				if(flag) {
					Thread.sleep(350);
					futureMessage.addUnicodeReaction(three);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getDescription() {
		return "Gets the info on a medal from the NA/Global version.";
	}

	@Override
	public String getUsage() {
		return "!medalna [name]";
	}

}
