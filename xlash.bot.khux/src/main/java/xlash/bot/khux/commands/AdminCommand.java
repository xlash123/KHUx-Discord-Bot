package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

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
		Server server = message.getServer().get();
		ServerConfig config = KHUxBot.getServerConfig(server);
		if(args[0].equalsIgnoreCase("list")) {
			String admins = "";
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
				message.getChannel().sendMessage("Registered admins: " + admins);
			}else message.getChannel().sendMessage("There are no registered admins. Ask your server owner to designate bot admins!");
			return;
		}
		for(String arg : args) {
			if(arg.equalsIgnoreCase("@everyone")) {
				message.getChannel().sendMessage("That's a bad idea.");
				return;
			}
		}
		String newAdmins = "";
		int iterations = 0;
		for(User u : message.getMentionedUsers()){
			if(!config.admins.contains(u.getIdAsString())){
				config.admins.add(u.getIdAsString());
				newAdmins += UserUtil.getNickname(u, server) + ", ";
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
			if(iterations > 1) message.getChannel().sendMessage(newAdmins + " are now admins.");
			else message.getChannel().sendMessage(newAdmins + " is now an admin.");
			if(iterations > 0){
				config.saveConfig();
				return;
			}
		}else message.getChannel().sendMessage("No new admins have been added.");
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
