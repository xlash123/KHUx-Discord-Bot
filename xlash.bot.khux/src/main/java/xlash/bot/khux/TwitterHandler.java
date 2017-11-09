package xlash.bot.khux;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.btobastian.javacord.entities.Channel;

public class TwitterHandler {

	public long lastTwitterUpdateNA;
	public long lastTwitterUpdateJP;

	public TwitterHandler() {
		lastTwitterUpdateNA = getTwitterUpdateLink(0, GameEnum.NA).tweetId;
		lastTwitterUpdateJP = getTwitterUpdateLink(0, GameEnum.JP).tweetId;
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

		long max = 0;
		for(Tweet tweet : tweets) {
			if(tweet.tweetId > max) max = tweet.tweetId;
		}
		if(tweets.size()>0) {
			if(game == GameEnum.NA) {
				lastTwitterUpdateNA = max;
			}else {
				lastTwitterUpdateJP = max;
			}
		}
		return tweets.size()>0;
	}

	public ArrayList<Tweet> getNewTwitterLinks(GameEnum game) {
		ArrayList<Tweet> ret = new ArrayList<Tweet>();
		Tweet current;
		for (int i = 0; i < 8; i++) {
			current = getTwitterUpdateLink(i, game);
			if (game == GameEnum.NA) {
				if (current.tweetId > lastTwitterUpdateNA) {
					ret.add(current);
				} else
					break;
			} else {
				if (current.tweetId > lastTwitterUpdateJP) {
					ret.add(current);
				} else
					break;
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
		String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
		String link = "https://twitter.com/" + gameString + "/";
		try {
			Document doc = Jsoup.connect(link).get();
			ArrayList<String> links = new ArrayList<String>();
			ArrayList<Long> ids = new ArrayList<Long>();
			Long tweetId = 0L;
			try {
				tweetId = Long.parseLong(doc.getElementsByClass("stream-items js-navigable-stream").get(0)
						.getElementsByAttributeValueMatching("data-item-type", "tweet").get(recent)
						.attr("data-item-id"));
			} catch (IndexOutOfBoundsException e) {
				// If there are less than 10 tweets, worry about it
				System.err.println("Could get tweet of index " + recent);
				e.printStackTrace();
				return null;
			}
			return new Tweet(tweetId, game);
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
		
	}

}
