package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.Collection;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.PermissionState;
import de.btobastian.javacord.entities.permissions.PermissionType;
import de.btobastian.javacord.entities.permissions.Role;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.config.ServerConfig;

/**
 * Handles message execution and storage
 *
 */
public class CommandHandler {
	
	/**
	 * List of commands
	 */
	public ArrayList<CommandBase> commands = new ArrayList<CommandBase>();
	
	public CommandHandler(){}
	
	/**
	 * Registers a command so it can be called by a user
	 * @param command instance of a command to register
	 */
	public void registerCommand(CommandBase command){
		commands.add(command);
	}
	
	/**
	 * Attempts to execute a command from the user's message
	 * @param message user message
	 */
	public void executeCommand(Message message){
		String content = message.getContent();
		if(!content.startsWith("!")) return;
		String[] parts = content.split(" ");
		for(CommandBase com : commands){
			for(String alias : com.getAliases()){
				if(alias.equalsIgnoreCase(parts[0])){
					if(com.isAdmin()){
						User user = message.getAuthor();
						boolean admin = false;
						ServerConfig config = KHUxBot.getServerConfig(message.getChannelReceiver().getServer());
						for(String id : config.admins){
							if(user.getId().equals(id)){
								admin = true;
								break;
							}
						}
						Collection<Role> roles = user.getRoles(message.getChannelReceiver().getServer());
						for(Role r : roles){
							if(r.getPermissions().getState(PermissionType.ADMINISTRATOR)==PermissionState.ALLOWED){
								admin = true;
							}
						}
						if(!admin){
							message.reply("Only admins may use this command.");
							return;
						}
					}
					String argsString = content.substring(content.indexOf(" ")+1);
					if(argsString.equals(content)) argsString = "";
					String[] argsArray = argsString.split(" ");
					if(argsArray[0].isEmpty()) argsArray = new String[0];
					if(com.isServerOnly() && message.isPrivateMessage()) {
						message.reply("Sorry, this command is for servers only.");
					}else com.onCommand(argsArray, message);
				}
			}
		}
	}

}
