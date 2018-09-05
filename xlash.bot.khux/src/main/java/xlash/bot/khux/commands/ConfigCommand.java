package xlash.bot.khux.commands;

import org.javacord.api.entity.message.Message;

import xlash.bot.khux.config.ServerConfig;

/**
 * Loads the config file
 *
 */
public class ConfigCommand extends CommandBase{

	@Override
	public String[] getAliases() {
		return new String[]{"!config"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length<1){
			this.printDescriptionUsage(message);
		}else{
			ServerConfig config = this.getServerConfig(message);
			String property = "";
			switch(args[0].toLowerCase()){
			case "lux":
				property += "Lux";
				if(args.length<3){
					this.printDescriptionUsage(message);
					return;
				}
				break;
			case "uc":
				property += "UC";
				if(args.length<3){
					this.printDescriptionUsage(message);
					return;
				}
				break;
			case "get":
				message.getChannel().sendMessage("Lux On: " + config.luxOnPrompt + "\nLux Off: " + config.luxOffPrompt + "\nUC On: " + config.ucOnPrompt + "\nUC Off: " + config.ucOffPrompt);
				return;
				default:
					this.printDescriptionUsage(message);
					return;
			}
			property += "_";
			switch(args[1].toLowerCase()) {
			case "on":
				property += "On";
				break;
			case "off":
				property += "Off";
				break;
				default:
					this.printDescriptionUsage(message);
					return;
			}
			property += "_Prompt";
			String mes = "";
			for (int i = 2; i < args.length; i++) {
				mes += args[i] + " ";
			}
			mes = mes.substring(0, mes.length()-1);
			config.putConfig(property, mes);
			message.getChannel().sendMessage("Config change saved!");
		}
	}

	@Override
	public String getDescription() {
		return "Makes changes to your server's configuration.";
	}

	@Override
	public String getUsage() {
		return "!config [lux/uc/get] [on/off] [message]";
	}
	
	@Override
	public boolean isAdmin() {
		return true;
	}

}
