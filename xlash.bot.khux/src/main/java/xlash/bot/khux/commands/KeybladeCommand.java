package xlash.bot.khux.commands;

import java.awt.Color;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;

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
			if(level > keyblade.getMaxLevel()) {
				level = keyblade.getMaxLevel();
			}else if(level < 0) level = 0;
			eb.setTitle(keyblade.name + " +" + KHUxBot.keybladeHandler.getAliasLevel(level));
			for(int i=0; i<keyblade.slots.length-1; i++) {
				Slot slot = keyblade.slots[i];
				String value = "";
				if(slot.slotActive > level) {
					value = "**Locked**";
				}else {
					float attrMult = slot.getAttrMultiplier(level);
					float typeMult = slot.getTypeMultiplier(level);
					if(attrMult > 1) {
						if(typeMult > 1) {
							value = "**"+slot.type.name+"**/*"+slot.attr.name+"*: **" + typeMult + "**/*" + attrMult + "*";
						}else value = "*"+slot.attr.name+"*: *"+attrMult+"*";
					}else value = "*No boost*";
				}
				eb.addField("Slot " + (i+1), value, true);
			}
			Slot friend = keyblade.slots[keyblade.slots.length-1];
			eb.addField("Friend Slot", ""+friend.getTypeMultiplier(level), true);
			eb.setFooter("All Keyblade information received from khuxtracker.com. Visit the website for more specific information.");
			eb.setColor(Color.green);
			message.getChannel().sendMessage("", eb);
		}else {
			eb.setColor(Color.red);
			eb.setDescription("I could not find that Keyblade.");
			message.getChannel().sendMessage("", eb);
		}
	}

	@Override
	public String getDescription() {
		return "Gets data for a certain Keyblade at the specified level.";
	}

	@Override
	public String getUsage() {
		return "!keyblade [keyblade name] (level)";
	}

}
