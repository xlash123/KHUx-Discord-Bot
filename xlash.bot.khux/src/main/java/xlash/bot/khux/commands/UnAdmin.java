package xlash.bot.khux.commands;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.UserUtil;

/**
 * Removes permissions to be able to use admin commands
 *
 */
public class UnAdmin extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!deadmin","!deop","!unadmin","!unop"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		for(String arg : args) {
			if(arg.equalsIgnoreCase("@everyone")) {
				message.getChannel().sendMessage("That's a bad idea.");
				return;
			}
		}
		Server server = message.getServer().get();
		ServerConfig config = this.getServerConfig(message);
		String unAdmins = "";
		int iterations = 0;
		for(User u : message.getMentionedUsers()){
			if(config.admins.contains(u.getIdAsString())){
				if(config.admins.size()<=1){
					message.getChannel().sendMessage("You must have at least one admin. " + UserUtil.getNickname(u, server) + " will remain admin.");
					return;
				}
				config.admins.remove(u.getIdAsString());
				unAdmins += UserUtil.getNickname(u, server) + ", ";
				iterations++;
			}
		}
		if(!unAdmins.isEmpty()){
			unAdmins = unAdmins.substring(0, unAdmins.length()-2);
			unAdmins = unAdmins.substring(0, unAdmins.lastIndexOf(',')+1) + " and" + unAdmins.substring(unAdmins.lastIndexOf(',')+1);
			if(iterations<2){
				unAdmins = unAdmins.replaceAll(",", "");
				unAdmins = unAdmins.substring(4);
			}
			if(iterations > 1) message.getChannel().sendMessage(unAdmins + " are no longer admins.");
			else message.getChannel().sendMessage(unAdmins + " is no longer an admin.");
			if(iterations > 0){
				config.saveConfig();
				return;
			}
		}else message.getChannel().sendMessage("No people have lost admin privileges.");
	}

	@Override
	public String getDescription() {
		return "Removes permissions to be able to use admin commands.";
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}

	@Override
	public String getUsage() {
		return "!unadmin @[user] @[another user]...";
	}
	
	@Override
	public boolean isServerOnly() {
		return true;
	}
	
}
