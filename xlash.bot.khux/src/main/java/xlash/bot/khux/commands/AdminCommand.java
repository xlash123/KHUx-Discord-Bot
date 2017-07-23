package xlash.bot.khux.commands;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.UserUtil;

public class AdminCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!admin","!op"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		if(args[0].equalsIgnoreCase("@everyone")){
			message.reply("No");
			return;
		}
		ServerConfig config = KHUxBot.getServerConfig(message.getChannelReceiver().getServer());
		String newAdmins = "";
		int iterations = 0;
		for(User u : message.getMentions()){
			if(!config.admins.contains(u.getId())){
				config.admins.add(u.getId());
			}
			newAdmins += UserUtil.getNickname(u, message.getChannelReceiver().getServer()) + ", ";
			iterations++;
		}
		if(!newAdmins.isEmpty()){
			newAdmins = newAdmins.substring(0, newAdmins.length()-2);
			newAdmins = newAdmins.substring(0, newAdmins.lastIndexOf(',')+1) + " and" + newAdmins.substring(newAdmins.lastIndexOf(',')+1);
			if(iterations<2){
				newAdmins = newAdmins.replaceAll(",", "");
				newAdmins = newAdmins.substring(4);
			}
		}
		if(iterations> 1) message.reply(newAdmins + " are now admins.");
		else message.reply(newAdmins + " is now an admin.");
		if(iterations > 0){
			config.saveConfig();
			return;
		}
		message.reply("Unknown user(s). You must @mention real users on this server.");
	}

	@Override
	public String getDescription() {
		return "Grants a user permission to admin bot commands. Be careful, as whomever you admin can use this command.";
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}

	@Override
	public String getUsage() {
		return "!admin @[user] @[another user]...";
	}

}
