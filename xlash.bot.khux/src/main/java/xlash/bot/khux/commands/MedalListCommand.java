package xlash.bot.khux.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import xlash.bot.khux.GameEnum;
import xlash.bot.khux.KHUxBot;

public class MedalListCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!medallist"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length == 0){
			this.printDescriptionUsage(message);
			return;
		}
		User u = message.getAuthor();
		String toSend = "Here is the current list of all the known medals and their nicknames:\n```";
		if(GameEnum.parseString(args[0]).equals(GameEnum.NA)){
			toSend += getList(KHUxBot.medalHandler.nicknames);
		}else{
			toSend += getList(KHUxBot.medalHandler.jpNicknames);
		}
		sendMessageUnderRate(toSend, u);
	}
	
	private String getList(HashMap<String, ArrayList<String>> nicknames){
		String out = "";
		Iterator<String> iKey = nicknames.keySet().iterator();
		Iterator<ArrayList<String>> iValue = nicknames.values().iterator();
		while(iKey.hasNext()){
			String realName = iKey.next();
			ArrayList<String> listOfNicks = iValue.next();
			out += "--" + realName + ": ";
			for(int i=0; i<listOfNicks.size(); i++){
				out += listOfNicks.get(i);
				if(i+1<listOfNicks.size()) {
					out += ", ";
				} else out += "\n";
			}
		}
		out += "```";
		return out;
	}
	
	private void sendMessageUnderRate(String initial, User u) {
		while(initial.length() != 0) {
			System.err.println("Initial has " + initial.length());
			String toSend;
			if(initial.length() > 2000) {
				toSend = initial.substring(0, 2000);
				int cutOff;
				toSend = toSend.substring(0, cutOff = toSend.lastIndexOf("\n"));
				if(toSend.length()+3 > 2000) {
					toSend = toSend.substring(0, cutOff = toSend.lastIndexOf("\n"));
				}
				toSend += "```";
				initial = "```" + initial.substring(cutOff, initial.length());
			}else {
				toSend = new String(initial);
				initial = "";
			}
			System.err.println("Just sent " + toSend.length() + ". More to go: " + initial.length());
			u.sendMessage(toSend);
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getDescription() {
		return "Sends a PM listing all of the known medals and their nicknames. This list is really long.";
	}

	@Override
	public String getUsage() {
		return "!medallist [NA/JP/Both]";
	}

}
