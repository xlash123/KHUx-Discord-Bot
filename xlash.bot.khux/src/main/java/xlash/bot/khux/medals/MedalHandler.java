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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.vdurmont.emoji.EmojiManager;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.Reaction;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.ActionMessage;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

/**
 * Manages all the medals
 *
 */
public class MedalHandler {
	
	public ArrayList<Medal> cachedMedalsNA = new ArrayList<>();
	public ArrayList<Medal> cachedMedalsJP = new ArrayList<>();
	
	public MedalHandler() {
		
	}
	
	/**
	 * Clears the cache of all medal databases.
	 */
	public void clearDatabase() {
		cachedMedalsNA.clear();
		cachedMedalsNA.clear();
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
			String searchQuery = "type=search&table=medals&search="+name+"&order=kid&asc=DESC&method=directory&user=&page=0&limit=5&jp="+jp;
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
			System.out.println(response);
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
		for(Medal m : cachedMedalsNA) {
			if(m.mid.equals(mid)) return m;
		}
		for(Medal m : cachedMedalsJP) {
			if(m.mid.equals(mid)) return m;
		}
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
			InputStream in = con.getInputStream();
			con.connect();
			StringBuilder sb = new StringBuilder();
			while(in.available()>0) {
				sb.append((char) in.read());
			}
			con.disconnect();
			String response = sb.toString().substring(1, sb.length()-1);
			Gson gson = new Gson();
			RawMedal raw = gson.fromJson(response, RawMedal.class);
			raw.mid = mid;
			raw.name = raw.name.replaceAll("Namine", "Namin\u00E9").replaceAll("Lumiere", "Lumi\u00E8re");
			Medal medal = raw.toMedal();
			if(game==GameEnum.NA) {
				if(cachedMedalsNA.contains(medal)) {
					cachedMedalsNA.add(medal);
				}
			}else {
				if(cachedMedalsNA.contains(medal) && cachedMedalsJP.contains(medal)) {
					cachedMedalsJP.add(medal);
				}
			}
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
		try {
			Message futureMessage = message.reply("", eb).get();
			KHUxBot.actionMessages.add(new ActionMessage(futureMessage) {
				@Override
				public void run(Reaction reaction) {
					Channel channel = futureMessage.getChannelReceiver();
					String unicode = reaction.getUnicodeEmoji();
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
					EmbedBuilder build = KHUxBot.medalHandler.prepareMedalMessage(medal);
					if(channel != null) {
						channel.sendMessage("", build);
					} else {
						message.getAuthor().sendMessage("", build);
					}
				}
			});
			futureMessage.addUnicodeReaction(one);
			//It needs to wait for the reaction to actually be added. I've tried using Future.isDone(), but that doesn't seem to work.
			//Could potentially break if speed is slow
			Thread.sleep(350);
			futureMessage.addUnicodeReaction(two);
			if(query.size()>2) {
				Thread.sleep(350);
				futureMessage.addUnicodeReaction(three);
				if(query.size()>3) {
					Thread.sleep(350);
					futureMessage.addUnicodeReaction(four);
					if(query.size()>4) {
						Thread.sleep(350);
						futureMessage.addUnicodeReaction(five);
					}
				}
			}
			Thread.sleep(350);
			futureMessage.addUnicodeReaction(cancel);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prepares the message to send to the user
	 * @param medal the selected medal
	 * @return An embeded message for the user to receive
	 */
	public EmbedBuilder prepareMedalMessage(Medal medal) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setTitle(medal.name);
		String imgLink = getImgLinkForMedal(medal);
		eb.setImage(imgLink);
		eb.addField("Special", StringEscapeUtils.unescapeHtml4(medal.special), true);
		eb.addField("Type/Attribute", medal.type.name+"/"+medal.attribute.name, true);
		eb.addField("Strength", ""+medal.strength, true);
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
		eb.setFooter("Medal information from khuxtracker.com. All info is displayed based off of lvl 100 with max dots. See website for more specific info.");
		return eb;
	}
	
	public String getImgLinkForMedal(Medal medal) {
		URL url;
		try {
			String encodedName = URLEncoder.encode(medal.name+" 6\u2605", "UTF-8");
			encodedName = encodedName.replaceAll("Limited", "LM").replaceAll("Nightmare", "NM");
			url = new URL("http://www.khunchainedx.com/w/index.php?title=Special:Search&profile=images&fulltext=1&search=" + encodedName);;
			Document doc = Jsoup.parse(url.openStream(), null, "");
			Elements elms = doc.getElementsByTag("tr");
			String link = elms.get(0).getElementsByTag("a").get(0).attr("href");
			if(link.contains("(Old)")) {
				link = elms.get(1).getElementsByTag("a").get(0).attr("href");
			}
			URL url2 = new URL("http://www.khunchainedx.com" + link);
			Document doc2 = Jsoup.parse(url2.openStream(), null, "");
			Elements elements = doc2.getElementsByTag("img");
			for(Element e : elements) {
				if(e.hasAttr("alt") && e.attr("alt").startsWith("File:")) {
					return "http://www.khunchainedx.com" + e.attr("src");
				}
			}
		} catch (IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return "";
	}

}
