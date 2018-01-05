package xlash.bot.khux.medals;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

public class MedalHandler {
	
	public ArrayList<Medal> cachedMedalsNA = new ArrayList<>();
	public ArrayList<Medal> cachedMedalsJP = new ArrayList<>();
	
	private boolean disabled;
	
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
			name = URLEncoder.encode(name, "UTF-8");
			String searchQuery = "type=search&table=medals&search="+name+"&order=kid&asc=DESC&method=directory&user=&page=0&limit=3&jp="+jp;
			HttpURLConnection con = (HttpURLConnection) new URL("http://khuxtracker.com/query.php").openConnection();
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

		for(Medal m : cachedMedalsNA) {
			if(m.mid.equals(mid)) return m;
		}
		for(Medal m : cachedMedalsJP) {
			if(m.mid.equals(mid)) return m;
		}
		try {
			String searchQuery = "id=" + mid + "&type=view&method=directory";
			HttpURLConnection con = (HttpURLConnection) new URL("http://khuxtracker.com/query.php").openConnection();
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
	
	public EmbedBuilder prepareMedalMessage(Medal medal) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setColor(Color.GREEN);
		eb.setAuthor(medal.name);
		eb.setImage(getImgLinkForMedal(medal));
		eb.addField("Special", StringEscapeUtils.unescapeHtml4(medal.special), true);
		eb.addField("Type/Attribute", medal.type.name+"/"+medal.attribute.name, true);
		eb.addField("Strength", ""+medal.strength, true);
		eb.addField("Guages", ""+medal.guages, true);
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
			url = new URL("http://www.khunchainedx.com/wiki/File:"+medal.name.replaceAll(" ", "_").replaceAll("\\[", "(").replaceAll("\\]", ")")+"_6\u2605_KHUX.png");
			Document doc = Jsoup.connect(url.toString()).get();
			Elements elements = doc.getElementsByTag("img");
			for(Element e : elements) {
				if(e.hasAttr("alt") && e.attr("alt").startsWith("File:")) {
					return "http://www.khunchainedx.com" + e.attr("src");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
