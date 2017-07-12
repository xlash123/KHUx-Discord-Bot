package xlash.bot.khux.util;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;

public class UserUtil {
	
	public static String getNickname(User u, Server s) {
		String nickname = u.getNickname(s);
		if(nickname==null||nickname.isEmpty()) {
			nickname = u.getName();
		}
		return nickname;
	}

}
