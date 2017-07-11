package xlash.bot.khux.commands;

import java.util.ArrayList;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.KHUxBot;

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
			for(String s : com.getAliases()){
				if(s.equalsIgnoreCase(parts[0])){
					if(com.isAdmin()){
						User user = message.getAuthor();
						boolean admin = false;
						for(String id : KHUxBot.config.admins){
							if(user.getId().equals(id)){
								admin = true;
								break;
							}
						}
						if(!admin){
							message.reply("Only admins may use this command.");
							return;
						}
					}
					String argsString = content.substring(content.indexOf(" ")+1);
					com.onCommand(argsString.split(" "), message);
				}
			}
		}
	}

}
