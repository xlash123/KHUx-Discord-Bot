package xlash.bot.khux.medals;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import xlash.bot.khux.medals.SearchQuery.MedalQuery;

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
			String searchQuery = "type=search&table=medals&search="+name+"&order=kid&asc=DESC&method=directory&user=&page=0" + "" + "&limit=10&jp="+jp;
			HttpURLConnection con = (HttpURLConnection) new URL("https://khuxtracker.com/query.php").openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			
			try(OutputStream out = con.getOutputStream()){
				out.write(searchQuery.getBytes());
			}
			Scanner in = new Scanner(con.getInputStream());
			con.connect();
			StringBuilder sb = new StringBuilder();
			while(in.hasNext()) {
				sb.append(in.next() + " ");
			}
			in.close();
			con.disconnect();
			String response = "{queries:"+sb.toString()+"}";
//			System.out.println(response);
			Gson gson = new Gson();
			SearchQuery res = gson.fromJson(response, SearchQuery.class);
			//Remove duplicates due to 6 and 7 stars
			for(int i=0; i<res.queries.size(); i++) {
				MedalQuery q = res.queries.get(i);
				if(res.queries.subList(0, i).stream().filter(qr -> qr.mid.equals(q.mid)).count()>0 || res.queries.subList(i+1, res.queries.size()).stream().filter(qr -> qr.mid.equals(q.mid)).count()>0) {
					res.queries.remove(i);
					i--;
				}
			}
			//Truncate list to 5 items
			List<MedalQuery> subList = res.queries.subList(0, res.queries.size()<5 ? res.queries.size() : 5);
			res.queries = new ArrayList<>();
			res.queries.addAll(subList);
			
			return res;
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
			String searchQuery = "id=" + mid + "&type=build&method=addmedal&rarity=7";
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
//			System.out.println(response);
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
		String fireworks = EmojiManager.getForAlias("fireworks").getUnicode();
		String plus = EmojiManager.getForAlias("heavy_plus_sign").getUnicode();
		TextChannel channel = message.getChannel();
		EmbedBuilder build = prepareMedalMessage(medal, false, false, false);
		Messageable receiver;
		if(channel != null) {
			receiver = channel;
		} else {
			receiver = message.getUserAuthor().get();
		}
		receiver.sendMessage(build).thenAcceptAsync(mes -> {
			if(medal.hasSeven() && medal.hasSix()) {
				mes.addReaction(seven);
			}
			if(medal.isUnlockable()) {
				mes.addReaction(plus);
			}
			if(medal.hasSupernova()) {
				mes.addReaction(fireworks);
			}
			if((medal.hasSeven() && medal.hasSix()) || medal.isUnlockable() || medal.hasSupernova()) {
				KHUxBot.actionMessages.add(new ActionMessage(mes, false) {
					@Override
					public void run(Reaction reaction, ActionMessage.Type type) {
						if(reaction.getEmoji().isUnicodeEmoji()) {
							String emoji = reaction.getEmoji().asUnicodeEmoji().get();
							if(type == ActionMessage.Type.ADD) {
								//Edit message to view 7 star
								if(emoji.equals(seven)) {
									mes.edit(prepareMedalMessage(medal, true, false, false));
								}
								else if(emoji.equals(plus)) {
									mes.edit(prepareMedalMessage(medal, true, true, this.supernova));
									this.unlocked = true;
								}
								else if(emoji.equals(fireworks)) {
									mes.edit(prepareMedalMessage(medal, true, medal.isUnlockable() ? this.unlocked : false, true));
									this.supernova = true;
								}
							}else {
								//Edit message to view 6 star
								if(emoji.equals(seven)) mes.edit(prepareMedalMessage(medal, false, false, false));
								//Edit message to show
								else if(emoji.equals(plus)) {
									mes.edit(prepareMedalMessage(medal, true, false, this.supernova));
									this.unlocked = false;
								}
								else if(emoji.equals(fireworks)) {
									mes.edit(prepareMedalMessage(medal, true, medal.isUnlockable() ? this.unlocked : false, false));
									this.supernova = false;
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
	public EmbedBuilder prepareMedalMessage(Medal m, boolean isSeven, boolean isUnlocked, boolean isSn) {
		System.out.println("Creating message: " + isSeven + " " + isUnlocked + " " + isSn);
		DecimalFormat df = new DecimalFormat("#.##");
		if(!m.hasSix()) isSeven = true;
		MedalDetails medal = isSeven ? m.getSeven() : m.getSix();
		if(isUnlocked) medal = m.getUnlocked();
		if(medal.supernova == null) isSn = false;
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		if(isSn) {
			Supernova sn = medal.supernova;
			eb.setTitle(medal.name + " - Supernova" + (isUnlocked ? "+" : ""));
			eb.addField("Special", StringEscapeUtils.unescapeHtml4(sn.special).replaceAll("<b>|<\\/b>", "**"), true);
			String range = ""+sn.baseLow;
			String range2 = ""+sn.maxLow;
			if(sn.baseLow != sn.baseHigh) range += " - "+sn.baseHigh;
			if(sn.maxLow != sn.maxHigh) range2 += " - "+sn.maxHigh;
			if(sn.baseLow > 0) eb.addField("Low Mult.", range, true);
			eb.addField("Mult.", range2, true);
			eb.addField("Target", sn.target.name, true);
		}else {
			eb.setTitle(medal.name + " - " + (isSeven ? "7" : "6") + "\u2605");
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
			eb.addField("Gauges", medal.gauges+"+"+medal.gaugesAdded+"; Net="+(medal.gaugesAdded+medal.gauges), true);
			eb.addField("Tier", ""+medal.tier.tier, true);
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
		}
		String imgLink = "http://www.khunchainedx.com/w/images" + medal.img;
		eb.setThumbnail(imgLink);
		eb.setFooter("Medal information from khuxtracker.com. All info is generally based on max stats. See website for more specific info. Options below will not work ~48 hours after this message was initially sent.");
		return eb;
	}
	
//	private byte[] getScaledImage(String urlS) {
//		try {
//			URL url = new URL(urlS);
//			BufferedImage img = ImageIO.read(url);
//			if(img == null) return null;
//			int w = img.getWidth();
//			int h = img.getHeight();
//			BufferedImage after = new BufferedImage(w/2, h/2, BufferedImage.TYPE_INT_ARGB);
//			AffineTransform at = new AffineTransform();
//			at.scale(0.5, 0.5);
//			AffineTransformOp scaleOp = 
//			   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
//			after = scaleOp.filter(img, after);
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			ImageIO.write(after, "PNG", baos);
//			return baos.toByteArray();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
}
