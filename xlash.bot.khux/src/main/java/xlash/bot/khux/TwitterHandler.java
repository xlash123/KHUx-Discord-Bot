package xlash.bot.khux;

import java.util.ArrayList;
import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import xlash.bot.khux.config.ServerConfig;

/**
 * Manages all the Twitter functionality. No, I'm not going to use their API.
 *
 */
public class TwitterHandler {

	/**
	 * Stores the last 10 Tweets for NA
	 */
	public ArrayList<Tweet> latestTweetsNA;
	/**
	 * Stores the last 10 Tweets for JP
	 */
	public ArrayList<Tweet> latestTweetsJP;;

	public TwitterHandler() {
		latestTweetsNA = this.getCurrentTweetList(GameEnum.NA);
		latestTweetsJP = this.getCurrentTweetList(GameEnum.JP);
	}
	
	public boolean sendNewUpdates(Collection<ServerConfig> configs, GameEnum game) {
		ArrayList<Tweet> tweets = this.getNewTwitterLinks(game, true);
		if(tweets.isEmpty()) return false;
		
		if(configs != null) {
			for(ServerConfig config: configs) {
				sendNewUpdates(config, game, tweets);
			}
		}
		
		return true;
	}
	
	public void sendNewUpdates(ServerConfig config, GameEnum game, ArrayList<Tweet> tweets) {
		String channelId = game == GameEnum.NA ? config.updateChannelNA : config.updateChannelJP;
		if(!channelId.isEmpty()){
			KHUxBot.api.getTextChannelById(channelId).ifPresent(channel -> {
				for (Tweet tweet : tweets) {
					if (tweet != null)
						channel.sendMessage(tweet.getLink());
				}
			});
		}
	}

	/**
	 * Gets the new Tweets detected
	 * 
	 * @param game
	 * @param updateArchive
	 *            will this modify the existing archive? If true, then running this
	 *            command immediately a second time will return nothing.
	 * @return
	 */
	public ArrayList<Tweet> getNewTwitterLinks(GameEnum game, boolean updateArchive) {
		ArrayList<Tweet> ret = new ArrayList<Tweet>();

		ArrayList<Tweet> current = this.getCurrentTweetList(game);
		ArrayList<Tweet> archive = new ArrayList<>();
		if (game == GameEnum.NA) {
			archive.addAll(latestTweetsNA);
		} else {
			archive.addAll(latestTweetsJP);
		}

		for (int i = 0; i < current.size(); i++) {
			Tweet t = current.get(i);
			if (!archive.contains(t)) {
				ret.add(t);
				archive.add(0, t);
			}
		}

		for (int i = 0; i < ret.size(); i++) {
			archive.remove(archive.size() - 1);
		}

		if (updateArchive) {
			if (game == GameEnum.NA) {
				latestTweetsNA.clear();
				latestTweetsNA.addAll(archive);
			} else {
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
		if (recent > 9) {
			return null;
		}
		ArrayList<Tweet> archive;
		if (game == GameEnum.NA) {
			archive = this.latestTweetsNA;
		} else {
			archive = this.latestTweetsJP;
		}
		return archive.get(recent);
	}

	/**
	 * The a current list of the first 10 Tweets listed on their Twitter page.
	 * 
	 * @param game
	 * @return
	 */
	public ArrayList<Tweet> getCurrentTweetList(GameEnum game) {
		int tries = 0;
		do {
			String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
			String link = "https://twitter.com/" + gameString + "/";
			ArrayList<Tweet> ret = new ArrayList<>();
			try {
				Document doc = Jsoup.connect(link).get();
				for (int i = 0; i < 10; i++) {
					Elements list = doc.getElementsByClass("stream-items js-navigable-stream").get(0)
							.getElementsByAttributeValueMatching("data-item-type", "tweet");
					for (int ii = 0; ii < 10 && ii < list.size(); ii++) {
						long tweetId = Long.parseLong(list.get(ii).attr("data-item-id"));
						ret.add(new Tweet(tweetId, game));
					}
				}
				return ret;
			} catch (Exception e) {
				tries++;
				System.err.println("Failed to get update from Twitter.");
				e.printStackTrace();
			}
		} while (tries < 3);
		return null;
	}

	/**
	 * A class for storing some info about a tweet
	 *
	 */
	public class Tweet {

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
			if (o instanceof Tweet) {
				Tweet t = (Tweet) o;
				return t.tweetId == this.tweetId;
			}
			return false;
		}

	}

}