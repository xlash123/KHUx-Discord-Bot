package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
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
				message.reply("That's a bad idea.");
				return;
			}
		}
		ServerConfig config = this.getServerConfig(message);
		String unAdmins = "";
		int iterations = 0;
		for(User u : message.getMentions()){
			if(config.admins.contains(u.getId())){
				if(config.admins.size()<=1){
					message.reply("You must have at least one admin. " + UserUtil.getNickname(u, message.getChannelReceiver().getServer()) + " will remain admin.");
					return;
				}
				config.admins.remove(u.getId());
				unAdmins += UserUtil.getNickname(u, message.getChannelReceiver().getServer()) + ", ";
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
			if(iterations > 1) message.reply(unAdmins + " are no longer admins.");
			else message.reply(unAdmins + " is no longer an admin.");
			if(iterations > 0){
				config.saveConfig();
				return;
			}
		}else message.reply("No people have lost admin privileges.");
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
