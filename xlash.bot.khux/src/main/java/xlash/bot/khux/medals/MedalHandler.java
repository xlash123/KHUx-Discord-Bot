package xlash.bot.khux.medals;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Scanner;

import org.apache.commons.text.StringEscapeUtils;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vdurmont.emoji.EmojiManager;

import xlash.bot.khux.ActionMessage;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

/**
 * Manages all the medals
 *
 */
public class MedalHandler {
	
	public MedalHandler() {
	}
	
	/**
	 * Uses khuxtracker.com to search for the medal name given.
	 * @param name
	 * @param game
	 * @return
	 */
	public SearchQuery searchMedalByName(String name, GameEnum game) {
		try {
			int jp = 0;
			if(game==GameEnum.JP) jp = 1;
			name = name.toLowerCase();
			name = name.replaceAll(" and ", " & ");
			name = URLEncoder.encode(name, "UTF-8");
			//Filters only the 6* medals to avoid double results
			String filter = URLEncoder.encode("where[0][filter]", "UTF-8") + "=rarity&" + URLEncoder.encode("where[0][type]", "UTF-8") + "=group&" + URLEncoder.encode("where[0][value][]", "UTF-8") + "=6";
			String searchQuery = "type=search&table=medals&search="+name+"&order=kid&asc=DESC&method=directory&user=&page=0&" + filter + "&limit=5&jp="+jp;
			HttpURLConnection con = (HttpURLConnection) new URL("https://khuxtracker.com/query.php").openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			try(OutputStream out = con.getOutputStream()){
				out.write(searchQuery.getBytes());
			}
			InputStream in = con.getInputStream();
			con.connect();
			StringBuilder sb = new StringBuilder();
			while(in.available()>0) {
				sb.append((char) in.read());
			}
			con.disconnect();
			String response = "{queries:"+sb.toString()+"}";
			Gson gson = new Gson();
			return gson.fromJson(response, SearchQuery.class);
		} catch (IOException e) {
			System.err.println("An error occured while searching for: " + name);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Uses the mid received from the search to lookup the medal. Checks cache first, then khuxtracker.com
	 * @param mid
	 * @param game
	 * @return
	 */
	public Medal getMedalByMid(String mid, GameEnum game) {
		try {
			String searchQuery = "id=" + mid + "&type=view&method=directory";
			HttpURLConnection con = (HttpURLConnection) new URL("https://khuxtracker.com/query.php").openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			try(OutputStream out = con.getOutputStream()){
				out.write(searchQuery.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Scanner in = new Scanner(con.getInputStream());
			con.connect();
			StringBuilder sb = new StringBuilder();
			while(in.hasNext()) {
				sb.append(in.next() + " ");
			}
			in.close();
			con.disconnect();
			String response = sb.toString();
			Gson gson = new GsonBuilder().registerTypeAdapter(Medal.class, new MedalDeserializer()).create();
			Medal medal = gson.fromJson(response, Medal.class);
			return medal;
		} catch (IOException e) {
			System.err.println("An error occured while searching for mid: " + mid);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Promps the user with the query of which medal they would like to see
	 * @param query the list of medals to query
	 * @param message the message the user send
	 * @param game game context
	 */
	public void promptQuery(SearchQuery query, Message message, GameEnum game) {
		EmbedBuilder eb = new EmbedBuilder();
		String one = EmojiManager.getForAlias("one").getUnicode();
		String two = EmojiManager.getForAlias("two").getUnicode();
		String three = EmojiManager.getForAlias("three").getUnicode();
		String four = EmojiManager.getForAlias("four").getUnicode();
		String five = EmojiManager.getForAlias("five").getUnicode();
		String cancel = EmojiManager.getForAlias("x").getUnicode();
		eb.setColor(Color.YELLOW);
		eb.setTitle("Did you mean...");
		eb.addField(one, query.queries.get(0).name, true);
		eb.addField(two, query.queries.get(1).name, true);
		if(query.size() > 2) eb.addField(three, query.queries.get(2).name, true);
		if(query.size() > 3) eb.addField(four, query.queries.get(3).name, true);
		if(query.size() > 4) eb.addField(five, query.queries.get(4).name, true);
		eb.setFooter("Click or tap on the reaction that corresponds with the medal you want.");
		message.getChannel().sendMessage("", eb).thenAcceptAsync(futureMessage -> {
			KHUxBot.actionMessages.add(new ActionMessage(futureMessage) {
				@Override
				public void run(Reaction reaction, ActionMessage.Type type) {
					String unicode = reaction.getEmoji().asUnicodeEmoji().get();
					String choice = "";
					if(unicode.equals(one)) {
						choice += query.queries.get(0).mid;
					}else if(unicode.equals(two)){
						choice += query.queries.get(1).mid;
					}else if(unicode.equals(three)){
						choice += query.queries.get(2).mid;
					}else if(unicode.equals(four)){
						choice += query.queries.get(3).mid;
					}else if(unicode.equals(five)){
						choice += query.queries.get(4).mid;
					}else if(unicode.equals(cancel)){
						futureMessage.delete();
						return;
					}else return;
					futureMessage.delete();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Medal medal = KHUxBot.medalHandler.getMedalByMid(choice, game);
					createMedalMessage(medal, message);
				}
			});
			futureMessage.addReaction(one);
			futureMessage.addReaction(two);
			if(query.size()>2) {
				futureMessage.addReaction(three);
				if(query.size()>3) {
					futureMessage.addReaction(four);
					if(query.size()>4) {
						futureMessage.addReaction(five);
					}
				}
			}
			futureMessage.addReaction(cancel);
		});
	}
	
	/**
	 * Creates and sends the medal message to the channel specified by the message
	 * @param medal
	 * @param message
	 */
	public void createMedalMessage(Medal medal, Message message) {
		String seven = EmojiManager.getForAlias("seven").getUnicode();
		TextChannel channel = message.getChannel();
		EmbedBuilder build = KHUxBot.medalHandler.prepareMedalMessage(medal, false);
		Messageable receiver;
		if(channel != null) {
			receiver = channel;
		} else {
			receiver = message.getUserAuthor().get();
		}
		receiver.sendMessage(build).thenAcceptAsync(mes -> {
			if(medal.hasSeven()) {
				mes.addReaction(seven);
				KHUxBot.actionMessages.add(new ActionMessage(mes, false) {
					@Override
					public void run(Reaction reaction, ActionMessage.Type type) {
						if(reaction.getEmoji().isUnicodeEmoji()) {
							String emoji = reaction.getEmoji().asUnicodeEmoji().get();
							if(emoji.equals(seven)) {
								if(type == ActionMessage.Type.ADD) {
									//Edit message to view 7 star
									mes.edit(KHUxBot.medalHandler.prepareMedalMessage(medal, true));
								}else {
									//Edit message to view 6 star
									mes.edit(KHUxBot.medalHandler.prepareMedalMessage(medal, false));
								}
							}
						}
					}
					public boolean test(ActionMessage.Type type) {
						return true;
					}
				});
			}
		});
	}
	
	/**
	 * Prepares the message to send to the user
	 * @param medal the selected medal
	 * @return An embeded message for the user to receive
	 */
	public EmbedBuilder prepareMedalMessage(Medal m, boolean isSeven) {
		MedalDetails medal = isSeven ? m.getSeven() : m.getSix();
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setTitle(medal.name + " - " + (isSeven ? "7" : "6") + "\u2605");
		String imgLink = "http://www.khunchainedx.com/w/images" + medal.img;
		eb.setImage(imgLink);
		if(medal.special.isEmpty()) { //Some 7* medals don't have an updated description, so replace it with 6*
			eb.addField("Special", StringEscapeUtils.unescapeHtml4(m.getSix().special).replaceAll("<b>|<\\/b>", "**"), true);
		}else {
			eb.addField("Special", StringEscapeUtils.unescapeHtml4(medal.special).replaceAll("<b>|<\\/b>", "**"), true);
		}
		eb.addField("Type/Attribute", medal.type.name+"/"+medal.attribute.name, true);
		if(isSeven && medal.strength < 1000) { //857 is the calculated result from a medal with 0 as the min_strength
			eb.addField("Strength", "Unknown", true);
		}else {
			eb.addField("Strength", ""+medal.strength, true);
		}
		eb.addField("Gauges", ""+medal.gauges, true);
		eb.addField("Tier", ""+medal.tier.tier, true);
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		String range = ""+medal.maxLow;
		String range2 = ""+df.format(medal.maxLow*medal.tier.guiltMultiplier);
		if(medal.maxLow != medal.maxHigh) {
			range += " - "+medal.maxHigh;
			range2 += " - "+df.format(medal.maxHigh*medal.tier.guiltMultiplier);
		}
		eb.addField("Multiplier", range, true);
		eb.addField("Mult. w/ Max Guilt", range2, true);
		eb.addField("Target", medal.target.name, true);
		eb.setFooter("Medal information from khuxtracker.com. All info is displayed based off of max level with max dots. See website for more specific info. 7 star toggling will not be available ~48 hours after the message appears.");
		return eb;
	}
	
}
