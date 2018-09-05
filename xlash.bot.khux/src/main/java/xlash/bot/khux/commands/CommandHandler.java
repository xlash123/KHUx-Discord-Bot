package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.Collection;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionState;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

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
					//Currently hasPermission doesn't work
//					if(PermissionsUtil.hasPermission(PermissionType.SEND_MESSAGES, message.getChannelReceiver().getServer(), message.getChannelReceiver(), KHUxBot.api.getYourself())) {
//						message.getAuthor().sendMessage("I do not have permission to send messages in #"+message.getChannelReceiver().getName()+". Please get your server admin to enable this.");
//						return;
//					}
					if(com.isAdmin()){
						User user = message.getUserAuthor().get();
						
						if(message.getServer().isPresent()) {
							Server server = message.getServer().get();
							boolean admin = false;
							ServerConfig config = KHUxBot.getServerConfig(server);
							for(String id : config.admins){
								if(user.getIdAsString().equals(id)){
									admin = true;
									break;
								}
							}
							if(!admin) {
								Collection<Role> roles = user.getRoles(server);
								for(Role r : roles){
									//This works, so if anyone tries to say otherwise, they're high
									if(r.getPermissions().getState(PermissionType.ADMINISTRATOR)==PermissionState.ALLOWED){
										admin = true;
									}
								}
							}
							if(!admin){
								message.getChannel().sendMessage("Only admins may use this command.");
								return;
							}
						}
						
					}
					String argsString = content.substring(content.indexOf(" ")+1);
					if(argsString.equals(content)) argsString = "";
					String[] argsArray = argsString.split(" ");
					if(argsArray[0].isEmpty()) argsArray = new String[0];
					if(com.isServerOnly() && message.isPrivate()) {
						message.getChannel().sendMessage("Sorry, this command is for servers only.");
					}else com.onCommand(argsArray, message);
				}
			}
		}
	}

}
