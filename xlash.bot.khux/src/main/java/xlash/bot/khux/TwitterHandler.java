package xlash.bot.khux;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.btobastian.javacord.DiscordAPI;

public class TwitterHandler {
	
	public String lastTwitterUpdate;
	
	public TwitterHandler(){
		lastTwitterUpdate = getTwitterUpdateLink(0);
	}
	
	public void getTwitterUpdate(DiscordAPI api){
		ArrayList<String> links = new ArrayList<String>();
		String current;
		for(int i=0; i<8; i++){
			current = getTwitterUpdateLink(i);
			if(!current.equalsIgnoreCase(lastTwitterUpdate)){
				links.add(current);
			}else break;
		}
		for(String link : links){
			if(link!=null) api.getChannelById(KHUxBot.config.updateChannel).sendMessage(link);
		}
		if(links.size()>0) lastTwitterUpdate = links.get(links.size()-1);
	}
	
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
