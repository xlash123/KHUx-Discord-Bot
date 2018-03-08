package xlash.bot.khux.commands;

import java.awt.Color;

import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;
import xlash.bot.khux.KHUxBot;
import xlash.bot.khux.keyblades.Keyblade;
import xlash.bot.khux.keyblades.Slot;

public class KeybladeCommand extends CommandBase {

	@Override
	public String[] getAliases() {
		return new String[] {"!keyblade"};
	}

	@Override
	public void onCommand(String[] args, Message message) {
		if(args.length < 1) {
			this.printDescriptionUsage(message);
			return;
		}
		String keybladeName = "";
		Keyblade keyblade;
		if(args.length>1) {
			for(int i=0; i<args.length-1; i++) {
				keybladeName += args[i] + " ";
			}
			keybladeName = keybladeName.substring(0, keybladeName.length()-1);
			keyblade = KHUxBot.keybladeHandler.getKeyblade(keybladeName);
			if(keyblade == null) {
				keybladeName = "";
				for(int i=0; i<args.length; i++) {
					keybladeName += args[i] + " ";
				}
				keybladeName = keybladeName.substring(0, keybladeName.length()-1);
				keyblade = KHUxBot.keybladeHandler.getKeyblade(keybladeName);
			}
		}else {
			for(int i=0; i<args.length; i++) {
				keybladeName += args[i] + " ";
			}
			keybladeName = keybladeName.substring(0, keybladeName.length()-1);
			keyblade = KHUxBot.keybladeHandler.getKeyblade(keybladeName);
		}
		EmbedBuilder eb = new EmbedBuilder();
		if(keyblade != null) {
			int level = keyblade.getMaxLevel();
			try {
				level = KHUxBot.keybladeHandler.getRealLevel(Float.parseFloat(args[args.length-1]));
			}catch (NumberFormatException e) {
				System.out.println("Error when parsing level. Using max level.");
			}
			eb.setTitle(keyblade.name + " +" + KHUxBot.keybladeHandler.getAliasLevel(level));
			for(int i=0; i<keyblade.slots.length-1; i++) {
				Slot slot = keyblade.slots[i];
				eb.addField("Slot " + (i+1), slot.attr.name+"/"+slot.type.name+": " + slot.getAttrMultiplier(level) + "/" + slot.getTypeMultipler(level), true);
			}
			Slot friend = keyblade.slots[keyblade.slots.length-1];
			eb.addField("Friend Slot", ""+friend.getTypeMultipler(level), true);
			eb.setFooter("All Keyblade information received from khuxtracker.com. Visit the website for more specific information.");
			eb.setColor(Color.green);
			message.reply("", eb);
		}else {
			eb.setColor(Color.red);
			eb.setDescription("I could not find that Keyblade.");
			message.reply("", eb);
		}
	}

	@Override
	public String getDescription() {
		return "Gets data for a certain Keyblade at the specified level.";
	}

	@Override
	public String getUsage() {
		return "!keyblade [keyblade name] [level]";
	}

}
