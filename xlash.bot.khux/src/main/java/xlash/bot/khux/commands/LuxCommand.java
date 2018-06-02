package xlash.bot.khux.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.channel.ServerChannel;
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
 * Alerts when double lux time is active to a given channel
 *
 */
public class LuxCommand extends CommandBase{
	
	@Override
	public String[] getAliases(){
		return new String[]{"!lux"};
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
			setTimes(message, config, game, false, false, false, false);
    		break;
		case "status":
			boolean[] timesNA = getTimes(config, GameEnum.NA);
			boolean[] timesJP = getTimes(config, GameEnum.JP);
			String strTimesNA = "";
			String strTimesJP = "";
			for(int i=0; i<4; i++) {
				if(timesNA[i]) strTimesNA += BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartNA[i]) + ", ";
			}
			for(int i=0; i<2; i++) {
				if(timesJP[i]) strTimesJP += BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartJP[i]) + ", ";
			}
			Optional<ServerChannel> naChannel = KHUxBot.api.getServerChannelById(config.luxChannelNA);
			Optional<ServerChannel> jpChannel = KHUxBot.api.getServerChannelById(config.luxChannelJP);
			if (naChannel.isPresent() && !strTimesNA.isEmpty()) {
				strTimesNA = strTimesNA.substring(0, strTimesNA.length()-2);
				message.getChannel().sendMessage("Double lux reminders for NA are set for channel: #"
						+ naChannel.get().getName()+"\n"+
						"The registered times for NA are " + strTimesNA + ".");
			}
			else
				message.getChannel().sendMessage("Double lux reminders for NA are currently turned off.");
			if (jpChannel.isPresent() && !strTimesJP.isEmpty()) {
				strTimesJP = strTimesJP.substring(0, strTimesJP.length()-2);
				message.getChannel().sendMessage("Double lux reminders for JP are set for channel: #"
						+ jpChannel.get().getName()+"\n"+
						"The registered times for JP are " + strTimesJP + ".");
			}
			else
				message.getChannel().sendMessage("Double lux reminders for JP are currently turned off.");
			return;
		case "check":
			int nextTime;
			if(isEnabled(config, game)) {
				nextTime = BonusTimes.luxTimeDifference(game, getTimes(config, game));
			}else nextTime = BonusTimes.luxTimeDifference(game);
			int mins = nextTime%60;
			int hours = nextTime/60;
			String timeS = "";
			if(hours > 0) {
				timeS += hours + " hours ";
				if(mins > 0) timeS += "and ";
			}
			if(mins > 0) timeS += mins + " minutes ";
			if(timeS.isEmpty()) {
				message.getChannel().sendMessage("Double lux for " + game.name() + " just went active!");
			}else message.getChannel().sendMessage("There are " + timeS + "until double lux is active for " + game.name() + ".");
			break;
		case "remind":
			if(args.length > 1) {
				try {
					int time = Integer.parseInt(args[1]);
					if(time > 30 || time < 0) {
						message.getChannel().sendMessage("Out of range. Enter a number 0-30 inclusive.");
					}else {
						message.getChannel().sendMessage("Lux reminder set for " + time + " minutes before active time.");
						config.luxRemind = time;
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
	
	public void setTimes(Message message, ServerConfig config, GameEnum game, boolean time0, boolean time1, boolean time2, boolean time3) {
		boolean[] times = new boolean[] {time0, time1, time2, time3};
		//Store all of these booleans as a single integer
		int selections = (time0 ? 1 : 0) | (time1 ? 1<<1 : 0) | (time2 ? 1<<2 : 0) | (time3 ? 1<<3 : 0);
		if(game==GameEnum.NA) {
			config.luxSelectionsNA = selections;
			config.luxChannelNA = config.luxSelectionsNA > 0 ? message.getChannel().getIdAsString() : "";
		}else {
			config.luxSelectionsJP = selections;
			config.luxChannelJP = config.luxSelectionsJP > 0 ? message.getChannel().getIdAsString() : "";
		}
		if(selections>0) {
			String strTimes = "";
			if(game==GameEnum.NA) {
				for(int i=0; i<4; i++) {
					if(times[i]) strTimes += BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartNA[i]) + ", ";
				}
			}else {
				for(int i=0; i<2; i++) {
					if(times[i]) strTimes += BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartJP[i]) + ", ";
				}
			}
			strTimes = strTimes.substring(0, strTimes.length()-2);
			message.getChannel().sendMessage("The registered Lux times for " + game + " are: " + strTimes + ".");
		}else message.getChannel().sendMessage("Lux reminders for " + game + " have been turned off.");
		config.saveConfig();
	}
	
	public static boolean[] getTimes(ServerConfig config, GameEnum game) {
		int selections = 0;
		if(game==GameEnum.NA) {
			selections = config.luxSelectionsNA;
		}else {
			selections = config.luxSelectionsJP;
		}
		//Reverting the stored booleans from the binary of the integer
		return new boolean[] {(selections & 1) > 0, (selections & (1<<1)) > 0, (selections & (1<<2)) > 0, (selections & (1<<3)) > 0};
	}
	
	public void createTimesPrompt(Message message, ServerConfig config, GameEnum game) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("Set Times for Raids");
		String[] emojis = new String[] {EmojiManager.getForAlias("one").getUnicode(), EmojiManager.getForAlias("two").getUnicode(), EmojiManager.getForAlias("three").getUnicode(), EmojiManager.getForAlias("four").getUnicode()};
		String check = EmojiManager.getForAlias("white_check_mark").getUnicode();
		if(game==GameEnum.NA) {
			eb.setDescription("Select which times you want (Pacific Time), then click " + check);
			for(int i=0; i<4; i++) {
				eb.addField(emojis[i], BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartNA[i]), true);
			}
		}else {
			eb.setDescription("Select which times you want (Japan Time), then click " + check);
			for(int i=0; i<2; i++) {
				eb.addField(emojis[i], BonusTimes.getTimeLocalized(BonusTimes.doubleLuxStartJP[i]), true);
			}
		}
		try {
			Message futureMessage = message.getChannel().sendMessage("", eb).get();
			int iterations = game==GameEnum.NA ? 4 : 2;
			for(int i=0; i<iterations; i++) {
				futureMessage.addReaction(emojis[i]);
				Thread.sleep(350);
			}
			futureMessage.addReaction(check);
			Thread.sleep(350);
			KHUxBot.actionMessages.add(new ActionMessage(futureMessage) {
				@Override
				public void run(Reaction reaction, ActionMessage.Type type) {
					Message messageStored = channel.getMessageById(messageId).join();
					int size = messageStored.getReactions().size()-1;
					int[] counts = new int[4];
					for(int i=0; i<size; i++) {
						counts[i] = messageStored.getReactions().get(i).getCount()-1;
					}
					futureMessage.delete();
					setTimes(message, config, game, counts[0]>0, counts[1]>0, counts[2]>0, counts[3]>0);
				}
				@Override
				public boolean test(ActionMessage.Type type) {
					Message messageStored = channel.getMessageById(messageId).join();
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
			return KHUxBot.api.getChannelById(config.luxChannelNA).isPresent() && hasTimes;
		}else return KHUxBot.api.getChannelById(config.luxChannelJP).isPresent() && hasTimes;
	}

	@Override
	public String getDescription() {
		return "Alerts when double lux time is active to a given channel.";
	}

	@Override
	public String getUsage() {
		return "!lux [on/off/status/check] (na/jp) or !lux remind [minutes]";
	}

	@Override
	public boolean isAdmin() {
		return true;
	}

}
