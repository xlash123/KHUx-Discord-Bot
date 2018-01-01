package xlash.bot.khux;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.btobastian.javacord.entities.Channel;

public class TwitterHandler {
	
	/**
	 * Stores the last 10 Tweets
	 */
	public ArrayList<Tweet> latestTweetsNA = new ArrayList<>();
	/**
	 * Stores the last 10 Tweets
	 */
	public ArrayList<Tweet> latestTweetsJP = new ArrayList<>();

	public TwitterHandler() {
		latestTweetsNA = this.getCurrentTweetList(GameEnum.NA);
		latestTweetsJP = this.getCurrentTweetList(GameEnum.JP);
	}

	/**
	 * Posts the newest Tweets to the channel specified in config.
	 * 
	 * @param api
	 *            The api that sends the message.
	 * @return boolean if something was sent.
	 */
	public boolean sendTwitterUpdate(Channel channel, ArrayList<Tweet> tweets, GameEnum game) {
		for (Tweet tweet : tweets) {
			if (tweet != null)
				channel.sendMessage(tweet.getLink());
		}
		
		return tweets.size()>0;
	}

	/**
	 * Gets the new Tweets detected
	 * @param game
	 * @param updateArchive will this modify the existing archive? If true, then running this command immediately a second time will return nothing.
	 * @return
	 */
	public ArrayList<Tweet> getNewTwitterLinks(GameEnum game, boolean updateArchive) {
		ArrayList<Tweet> ret = new ArrayList<Tweet>();
		
		ArrayList<Tweet> current = this.getCurrentTweetList(game);
		ArrayList<Tweet> archive = new ArrayList<>();
		if(game==GameEnum.NA) {
			archive.addAll(latestTweetsNA);
		}else {
			archive.addAll(latestTweetsJP);
		}
		
		for(int i=0; i<current.size(); i++) {
			Tweet t = current.get(i);
			if(!archive.contains(t)) {
				ret.add(t);
				archive.add(0, t);
			}
		}
		
		for(int i=0; i<ret.size(); i++) {
			archive.remove(archive.size()-1);
		}
		
		if(updateArchive) {
			if(game==GameEnum.NA) {
				latestTweetsNA.clear();
				latestTweetsNA.addAll(archive);
			}else {
				latestTweetsJP.clear();
				latestTweetsJP.addAll(archive);
			}
		}

		return ret;
	}

	/**
	 * Grabs the link from the specified index of newest Tweet.
	 * 
	 * @param recent
	 *            The index of the newest Tweet to get. 0 = newest. Max value of 9.
	 * @return The URL for the Tweet.
	 */
	public Tweet getTwitterUpdateLink(int recent, GameEnum game) {
		if (recent > 9){
			return null;
		}
		ArrayList<Tweet> archive;
		if(game==GameEnum.NA) {
			archive = this.latestTweetsNA;
		}else {
			archive = this.latestTweetsJP;
		}
		return archive.get(recent);
	}
	
	/**
	 * The a current list of the first 10 Tweets listed on their Twitter page.
	 * @param game
	 * @return
	 */
	public ArrayList<Tweet> getCurrentTweetList(GameEnum game){
		String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
		String link = "https://twitter.com/" + gameString + "/";
		ArrayList<Tweet> ret = new ArrayList<>();
		try {
			Document doc = Jsoup.connect(link).get();
			for(int i=0; i<10; i++) {
				Elements list = doc.getElementsByClass("stream-items js-navigable-stream").get(0)
						.getElementsByAttributeValueMatching("data-item-type", "tweet");
				for(int ii=0; ii<10 && ii<list.size(); ii++) {
					long tweetId = Long.parseLong(list.get(ii).attr("data-item-id"));
					ret.add(new Tweet(tweetId, game));
				}
			}
			return ret;
		} catch (Exception e) {
			System.out.println("Failed to get update from Twitter.");
			e.printStackTrace();
		}
		return null;
	}
	
	public class Tweet{
		
		public final long tweetId;
		private final GameEnum game;
		
		public Tweet(long tweetId, GameEnum game) {
			this.tweetId = tweetId;
			this.game = game;
		}
		
		public String getLink() {
			String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
			return "https://twitter.com/" + gameString + "/status/" + tweetId;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Tweet) {
				Tweet t = (Tweet) o;
				return t.tweetId == this.tweetId;
			}
			return false;
		}
		
	}

}