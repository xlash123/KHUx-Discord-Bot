package xlash.bot.khux;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;

public class TwitterHandler {
	
	public String lastTwitterUpdate;
	
	public TwitterHandler(){
		lastTwitterUpdate = getTwitterUpdateLink(0);
	}
	
	/**
	 * Posts the newest Tweets to the channel specified in config.
	 * @param api The api that sends the message.
	 * @return boolean if something was sent.
	 */
	public boolean sendTwitterUpdate(Channel channel){
		boolean flag = false;
		ArrayList<String> links = new ArrayList<String>();
		String current;
		for(int i=0; i<8; i++){
			current = getTwitterUpdateLink(i);
			if(!current.equalsIgnoreCase(lastTwitterUpdate)){
				links.add(0, current);
			}else break;
		}
		for(String link : links){
			flag = true;
			if(link!=null) channel.sendMessage(link);
		}
		if(links.size()>0) lastTwitterUpdate = links.get(links.size()-1);
		return flag;
	}
	
	/**
	 * Grabs the link from the specified index of newest Tweet.
	 * @param recent The index of the newest Tweet to get. 0 = newest.
	 * @return The URL for the Tweet.
	 */
	public String getTwitterUpdateLink(int recent){
		try {
			Document doc = Jsoup.connect("https://twitter.com/kh_ux_na").get();
			String id = doc.getElementsByClass("stream-items js-navigable-stream").get(0).getElementsByAttributeValueMatching("data-item-type", "tweet").get(recent).attr("data-item-id");
			return "https://twitter.com/kh_ux_na/status/"+id;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to get update from Twitter, but that won't stop me!");
		}
		return null;
	}

}
