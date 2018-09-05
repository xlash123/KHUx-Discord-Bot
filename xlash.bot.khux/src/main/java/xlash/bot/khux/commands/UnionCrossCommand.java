package xlash.bot.khux.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import com.vdurmont.emoji.EmojiManager;

import xlash.bot.khux.ActionMessage;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.BonusTimes;

/**
 * Reminds the server when the Union Cross bonus times are active
 *
 */
public class UnionCrossCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[] {"!uc", "!ux", "!unioncross"};
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
			createTimesPrompt(message, config, game);
			break;
		case "off":
			setTimes(message, config, game, false, false, false, false, false);
    		break;
		case "status":
			boolean[] timesNA = getTimes(config, GameEnum.NA);
			boolean[] timesJP = getTimes(config, GameEnum.JP);
			String strTimesNA = "";
			String strTimesJP = "";
			for(int i=0; i<timesNA.length; i++) {
				if(timesNA[i]) strTimesNA += BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartNA[i]) + ", ";
			}
			for(int i=0; i<timesJP.length; i++) {
				if(timesJP[i]) strTimesJP += BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartJP[i]) + ", ";
			}
			Optional<TextChannel> naChannel = KHUxBot.api.getTextChannelById(config.ucChannelNA);
			Optional<TextChannel> jpChannel = KHUxBot.api.getTextChannelById(config.ucChannelJP);
			if (naChannel.isPresent() && !strTimesNA.isEmpty()) {
				strTimesNA = strTimesNA.substring(0, strTimesNA.length()-2);
				message.getChannel().sendMessage("UC reminders for NA are set for channel: <#"
						+ naChannel.get().getIdAsString()+">\n"+
						"The registered times for NA are " + strTimesNA + ".");
			}
			else
				message.getChannel().sendMessage("UC reminders for NA are currently turned off.");
			if (jpChannel.isPresent() && strTimesJP.isEmpty()) {
				strTimesJP = strTimesJP.substring(0, strTimesJP.length()-2);
				message.getChannel().sendMessage("UC reminders for JP are set for channel: <#"
						+ jpChannel.get().getIdAsString()+">\n"+
						"The registered times for JP are " + strTimesJP + ".");
			}
			else
				message.getChannel().sendMessage("UC reminders for JP are currently turned off.");
			return;
		case "check":
			int nextTime;
			if(isEnabled(config, game)) {
				nextTime = BonusTimes.uxTimeDifference(game, getTimes(config, game));
			}else nextTime = BonusTimes.uxTimeDifference(game);
			int mins = nextTime%60;
			int hours = nextTime/60;
			String timeS = "";
			if(hours > 0) {
				timeS += hours + " hours ";
				if(mins > 0) timeS += "and ";
			}
			if(mins > 0) timeS += mins + " minutes ";
			if(timeS.isEmpty()) {
				message.getChannel().sendMessage("UC bonus time for " + game.name() + " just went active!");
			}else message.getChannel().sendMessage("There are " + timeS + "until UC bonus time is active for " + game.name() + ".");
			break;
		case "remind":
			if(args.length > 1) {
				try {
					int time = Integer.parseInt(args[1]);
					if(time > 30 || time < 0) {
						message.getChannel().sendMessage("Out of range. Enter a number 0-30 inclusive.");
					}else {
						message.getChannel().sendMessage("UC reminder set for " + time + " minutes before bonus time.");
						config.ucRemind = time;
					}
				}catch(NumberFormatException e) {
					message.getChannel().sendMessage("I don't think that's a number... Enter a number 0-30 inclusive.");
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
	
	public void setTimes(Message message, ServerConfig config, GameEnum game, boolean time0, boolean time1, boolean time2, boolean time3, boolean time4) {
		boolean[] times = new boolean[] {time0, time1, time2, time3, time4};
		//Store all of these booleans as a single integer
		int selections = (time0 ? 1 : 0) | (time1 ? 1<<1 : 0) | (time2 ? 1<<2 : 0) | (time3 ? 1<<3 : 0) | (time4 ? 1<<4 : 0);
		if(game==GameEnum.NA) {
			config.ucSelectionsNA = selections;
			config.ucChannelNA = config.ucSelectionsNA > 0 ? message.getChannel().getIdAsString() : "";
		}else {
			config.ucSelectionsJP = selections;
			config.ucChannelJP = config.ucSelectionsJP > 0 ? message.getChannel().getIdAsString() : "";
		}
		if(selections>0) {
			String strTimes = "";
			if(game==GameEnum.NA) {
				for(int i=0; i<times.length; i++) {
					if(times[i]) strTimes += BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartNA[i]) + ", ";
				}
			}else {
				for(int i=0; i<2; i++) {
					if(times[i]) strTimes += BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartJP[i]) + ", ";
				}
			}
			strTimes = strTimes.substring(0, strTimes.length()-2);
			message.getChannel().sendMessage("The registered UC times for " + game + " are: " + strTimes);
		}else message.getChannel().sendMessage("UC reminders for " + game + " have been turned off.");
		config.saveConfig();
	}
	
	public static boolean[] getTimes(ServerConfig config, GameEnum game) {
		int selections = 0;
		if(game==GameEnum.NA) {
			selections = config.ucSelectionsNA;
		}else {
			selections = config.ucSelectionsJP;
		}
		//Reverting the stored booleans from the binary of the integer
		return new boolean[] {(selections & 1) > 0, (selections & (1<<1)) > 0, (selections & (1<<2)) > 0, (selections & (1<<3)) > 0, (selections & (1<<4)) > 0};
	}
	
	public void createTimesPrompt(Message message, ServerConfig config, GameEnum game) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Set Times for Union Cross");
		String[] emojis = new String[] {EmojiManager.getForAlias("one").getUnicode(), EmojiManager.getForAlias("two").getUnicode(), EmojiManager.getForAlias("three").getUnicode(), EmojiManager.getForAlias("four").getUnicode(), EmojiManager.getForAlias("five").getUnicode()};
		String check = EmojiManager.getForAlias("white_check_mark").getUnicode();
		if(game==GameEnum.NA) {
			eb.setDescription("Select which times you want (Pacific Time), then click " + check);
			for(int i=0; i<5; i++) {
				eb.addField(emojis[i], BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartNA[i]), true);
			}
		}else {
			eb.setDescription("Select which times you want (Japan Time), then click " + check);
			for(int i=0; i<2; i++) {
				eb.addField(emojis[i], BonusTimes.getTimeLocalized(BonusTimes.uxBonusStartJP[i]), true);
			}
		}
		try {
			Message futureMessage = message.getChannel().sendMessage("", eb).get();
			int iterations = game==GameEnum.NA ? 5 : 2;
			for(int i=0; i<iterations; i++) {
				futureMessage.addReaction(emojis[i]);
				Thread.sleep(350);
			}
			futureMessage.addReaction(check);
			KHUxBot.actionMessages.add(new ActionMessage(futureMessage) {
				@Override
				public void run(Reaction reaction, ActionMessage.Type type) {
					Message messageStored = channel.getMessageById(this.messageId).join();
					int size = messageStored.getReactions().size()-1;
					int[] counts = new int[size+1];
					for(int i=0; i<size; i++) {
						counts[i] = messageStored.getReactions().get(i).getCount()-1;
					}
					futureMessage.delete();
					setTimes(message, config, game, counts[0]>0, counts[1]>0, counts[2]>0, counts[3]>0, counts[4]>0);
				}
				@Override
				public boolean test(ActionMessage.Type type) {
					Message messageStored = channel.getMessageById(this.messageId).join();
					int size = messageStored.getReactions().size();
					return messageStored.getReactions().get(size-1).getCount()>1;
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isEnabled(ServerConfig config, GameEnum game) {
		boolean hasTimes = false;
		for(boolean b : getTimes(config, game)) {
			if(b) {
				hasTimes = true;
				break;
			}
		}
		if(game==GameEnum.NA) {
			return KHUxBot.api.getChannelById(config.ucChannelNA).isPresent() && hasTimes;
		}else return KHUxBot.api.getChannelById(config.ucChannelJP).isPresent() && hasTimes;
	}

	@Override
	public String getDescription() {
		return "Reminds the server when the Union Cross bonus times are active.";
	}

	@Override
	public String getUsage() {
		return "!uc [on/off/status/check] (na/jp) or !uc remind [minutes]";
	}

}
