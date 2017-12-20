package xlash.bot.khux;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.btobastian.javacord.entities.Channel;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHandler {

	public long lastTwitterUpdateNA;
	public long lastTwitterUpdateJP;
	
	public Twitter twitter;

	public TwitterHandler() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(KHUxBot.botConfig.consumer);
		cb.setOAuthConsumerSecret(KHUxBot.botConfig.consumerSecret);
		cb.setOAuthAccessToken(KHUxBot.botConfig.access);
		cb.setOAuthAccessTokenSecret(KHUxBot.botConfig.accessSecret);
		twitter = new TwitterFactory(cb.build()).getInstance();
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
				if (current.created > lastTwitterUpdateNA) {
					ret.add(current);
				} else
					break;
			} else {
				if (current.created > lastTwitterUpdateJP) {
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
		String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
		Paging paging = new Paging(1, 15);
		Tweet tweet = null;
		try {
			List<Status> statuses = twitter.getUserTimeline(gameString, paging);
			ArrayList<Status> ordered = new ArrayList<>();
			ordered.add(statuses.get(0));
			for(int i=1; i<statuses.size(); i++) {
				Status status = statuses.get(i);
				if(status.getCreatedAt().compareTo(ordered.get(0).getCreatedAt()) > 0) {
					ordered.add(0, status);
				}else {
					ordered.add(status);
				}
			}
			tweet = new Tweet(ordered.get(0).getId(), ordered.get(0).getCreatedAt(), game);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return tweet;
	}
	
public class Tweet{
		
		public final long tweetId;
		public final long created;
		private final GameEnum game;
		
		public Tweet(long tweetId, Date created, GameEnum game) {
			this.tweetId = tweetId;
			this.created = created.getTime();
			this.game = game;
		}
		
		public String getLink() {
			String gameString = (game == GameEnum.NA ? "kh_ux_na" : "khux_pr");
			return "https://twitter.com/" + gameString + "/status/" + tweetId;
		}
		
	}

}
