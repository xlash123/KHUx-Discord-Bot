package xlash.bot.khux.util;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;

/**
 * Methods that are useful for the User class
 *
 */
public class UserUtil {
	
	/**
	 * Gets the nickname of a user
	 * @param u the user
	 * @param s the user's server
	 * @return the user's nickname
	 */
	public static String getNickname(User u, Server s) {
		String nickname = u.getNickname(s);
		if(nickname==null||nickname.isEmpty()) {
			nickname = u.getName();
		}
		return nickname;
	}

}
