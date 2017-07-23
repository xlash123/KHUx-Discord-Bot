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

public class CommandHandler {
	
	public ArrayList<CommandBase> commands = new ArrayList<CommandBase>();
	
	public CommandHandler(){}
	
	public void registerCommand(CommandBase command){
		commands.add(command);
	}
	
	public void executeCommand(Message message){
		String content = message.getContent();
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
							if(r.getPermissions().getState(PermissionType.ADMINISTATOR)==PermissionState.ALLOWED){
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
					com.onCommand(argsArray, message);
				}
			}
		}
	}

}
