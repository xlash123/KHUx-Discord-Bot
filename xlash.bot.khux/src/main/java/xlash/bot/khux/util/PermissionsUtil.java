package xlash.bot.khux.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.permissions.PermissionState;
import de.btobastian.javacord.entities.permissions.PermissionType;
import de.btobastian.javacord.entities.permissions.Role;

public class PermissionsUtil {
	
	/**
	 * A WIP method used to determine if the given permission is allowed for the given channel and user
	 * @param type
	 * @param server
	 * @param channel
	 * @param user
	 * @return
	 */
	public static boolean hasPermission(PermissionType type, Server server, Channel channel, User user) {
		Collection<Role> roles = user.getRoles(server);
		int offset = channel != null ? 1 : 0;
		ArrayList<PermissionState> states = new ArrayList<>();
		if(offset>0) states.add(channel.getOverwrittenPermissions(user).getState(type));
		Iterator<Role> iterator = roles.iterator();
		while(iterator.hasNext()) {
			Role role = iterator.next();
			states.add(channel.getOverwrittenPermissions(role).getState(type));
			states.add(role.getPermissions().getState(type));
		}
		boolean ret = true;
		for(PermissionState state : states) {
			if(state==PermissionState.DENIED) ret = false;
			break;
		}
		return ret;
	}

}
