package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;
import xlash.bot.khux.util.UserUtil;

/**
 * A command for managing people who use admin commands
 *
 */
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
		ServerConfig config = KHUxBot.getServerConfig(message.getChannelReceiver().getServer());
		if(args[0].equalsIgnoreCase("list")) {
			String admins = "";
			Server server = message.getChannelReceiver().getServer();
			ArrayList<String> badUsers = new ArrayList<>();
			for(String userId : config.admins) {
				try {
					System.out.println("Trying " + userId);
					User user = KHUxBot.api.getUserById(userId).get();
					if(user != null) {
						admins += UserUtil.getNickname(user, server) + ", ";
					}else {
						//Since this user couldn't be found, I'm removing it from the list so it doesn't happen again.
						badUsers.add(userId);
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			if(badUsers.size()>0) {
				config.admins.removeAll(badUsers);
				//Since it saves, running !admin list  should fix any weird admin errors.
				config.saveConfig();
			}
			if(!admins.isEmpty()) {
				admins = admins.substring(0, admins.length()-2);
				message.reply("Registered admins: " + admins);
			}else message.reply("There are no registered admins. Ask your server owner to designate bot admins!");
			return;
		}
		for(String arg : args) {
			if(arg.equalsIgnoreCase("@everyone")) {
				message.reply("That's a bad idea.");
				return;
			}
		}
		String newAdmins = "";
		int iterations = 0;
		for(User u : message.getMentions()){
			if(!config.admins.contains(u.getId())){
				config.admins.add(u.getId());
				newAdmins += UserUtil.getNickname(u, message.getChannelReceiver().getServer()) + ", ";
				iterations++;
			}
		}
		if(!newAdmins.isEmpty()){
			newAdmins = newAdmins.substring(0, newAdmins.length()-2);
			newAdmins = newAdmins.substring(0, newAdmins.lastIndexOf(',')+1) + " and" + newAdmins.substring(newAdmins.lastIndexOf(',')+1);
			if(iterations<2){
				newAdmins = newAdmins.replaceAll(",", "");
				newAdmins = newAdmins.substring(4);
			}
			if(iterations > 1) message.reply(newAdmins + " are now admins.");
			else message.reply(newAdmins + " is now an admin.");
			if(iterations > 0){
				config.saveConfig();
				return;
			}
		}else message.reply("No new admins have been added.");
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
		return "!admin @[user] @[another user]... or !admin list";
	}
	
	@Override
	public boolean isServerOnly() {
		return true;
	}

}
