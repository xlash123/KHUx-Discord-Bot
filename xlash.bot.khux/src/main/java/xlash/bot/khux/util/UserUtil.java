package xlash.bot.khux.util;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

/**
 * Methods that are useful for the User class
 *
 */
public class UserUtil {
	
	/**
	 * Gets the on-screen name of a user, typically the nickname
	 * @param u the user
	 * @param s the user's server
	 * @return the user's nickname
	 */
	public static String getNickname(User u, Server s) {
		return u.getNickname(s).orElse(u.getName());
	}

}
