package xlash.bot.khux;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.btobastian.javacord.entities.Channel;

public class TwitterHandler {
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	
	public String lastTwitterUpdateNA;
	public String lastTwitterUpdateJP;
	
	public TwitterHandler(){
		lastTwitterUpdateNA = getTwitterUpdateLink(0, GameEnum.NA);
		lastTwitterUpdateJP = getTwitterUpdateLink(0, GameEnum.JP);
	}
	
	/**
	 * Posts the newest Tweets to the channel specified in config.
	 * @param api The api that sends the message.
	 * @return boolean if something was sent.
	 */
	public boolean sendTwitterUpdate(Channel channel, ArrayList<String> links, GameEnum game){
		boolean flag = false;
		
		for(String link : links){
			flag = true;
			if(link!=null) channel.sendMessage(link);
		}
		
		if(links.size()>0) {
			if(game==GameEnum.NA) {
				lastTwitterUpdateNA = links.get(links.size()-1);
			}else {
				lastTwitterUpdateJP = links.get(links.size()-1);
			}
		}
		return flag;
	}
	
	public ArrayList<String> getNewTwitterLinks(GameEnum game){
		ArrayList<String> ret = new ArrayList<String>();
		String current;
		for(int i=0; i<8; i++){
			current = getTwitterUpdateLink(i, game);
			if(game==GameEnum.NA) {
				if(!current.equalsIgnoreCase(lastTwitterUpdateNA)){
					ret.add(0, current);
				}else break;
			}else {
				if(!current.equalsIgnoreCase(lastTwitterUpdateJP)){
					ret.add(0, current);
				}else break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Grabs the link from the specified index of newest Tweet.
	 * @param recent The index of the newest Tweet to get. 0 = newest.
	 * @return The URL for the Tweet.
	 */
	public String getTwitterUpdateLink(int recent, GameEnum game){
		String gameString = (game==GameEnum.NA ? "kh_ux_na" : "khux_pr");
		String link = "https://twitrss.me/twitter_user_to_rss/?user=" + gameString;
		try {
			Document doc = Jsoup.connect(link).get();
			Elements items = doc.getElementsByTag("item");
			ArrayList<String> links = new ArrayList<String>();
			ArrayList<Long> ids = new ArrayList<Long>();
			for(int i=0; i<10; i++) {
				String tweetLink = items.get(i).getElementsByTag("link").get(0).text();
				Long tweetId = Long.parseLong(tweetLink.substring(tweetLink.lastIndexOf("/")+1));
				if(links.size()==0) {
					links.add(tweetLink);
					ids.add(tweetId);
				}else {
					for(int j=0; j<ids.size(); j++) {
						if(tweetId>=ids.get(j)) {
							links.add(j, tweetLink);
							ids.add(j, tweetId);
							break;
						}
						if(j==ids.size()-1) {
							links.add(tweetLink);
							ids.add(tweetId);
						}
					}
				}
			}
			return links.get(recent);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get update from Twitter, but that won't stop me!");
		}
		return null;
	}

}
