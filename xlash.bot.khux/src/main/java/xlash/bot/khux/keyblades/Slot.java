package xlash.bot.khux.keyblades;

import java.util.ArrayList;

import xlash.bot.khux.medals.Attribute;
import xlash.bot.khux.medals.Medal;
import xlash.bot.khux.medals.Type;

public class Slot {
	
	public final Attribute attr;
	public final Type type;
	public final int attrActive, typeActive;
	
	public final ArrayList<Float> attrMultipliers, typeMultipliers;

	public Slot(Attribute attr, Type type, int attrActive, int typeActive, ArrayList<Float> attrMultipliers, ArrayList<Float> typeMultipliers) {
		this.attr = attr;
		this.type = type;
		this.attrActive = attrActive;
		this.typeActive = typeActive;
		this.attrMultipliers = attrMultipliers;
		this.typeMultipliers = typeMultipliers;
	}
	
	public double getSlotMultiplier(Attribute attr, Type type, int slotNumber, int level) {
		if(this.attr == attr) {
			if(this.type == type) {
				return getTypeMultipler(level);
			}
			else return getAttrMultiplier(level);
		}else return 1;
	}
	
	private double getAttrMultiplier(int level) {
		if(level>=attrActive) {
			return attrMultipliers.get(level-attrActive);
		} else return 1;
	}
	
	private double getTypeMultipler(int level) {
		if(level>=typeActive) {
			return typeMultipliers.get(level-typeActive);
		} else return 1;
	}
	
	public double getSlotMultipler(Medal medal, int slotNumber, int level) {
		return getSlotMultiplier(medal.attribute, medal.type, slotNumber, level);
	}
	
}
