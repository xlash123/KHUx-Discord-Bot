package xlash.bot.khux.keyblades;

import java.util.ArrayList;

import xlash.bot.khux.medals.Attribute;
import xlash.bot.khux.medals.Medal;
import xlash.bot.khux.medals.Type;

public class Slot {
	
	public final Attribute attr;
	public final Type type;
	public final int attrActive, typeActive;
	public final int slotActive;
	
	public final ArrayList<Float> attrMultipliers, typeMultipliers;

	public Slot(Attribute attr, Type type, int attrActive, int typeActive, int slotActive, ArrayList<Float> attrMultipliers, ArrayList<Float> typeMultipliers) {
		this.attr = attr;
		this.type = type;
		this.attrActive = attrActive;
		this.typeActive = typeActive;
		this.slotActive = slotActive;
		this.attrMultipliers = attrMultipliers;
		this.typeMultipliers = typeMultipliers;
	}
	
	public float getSlotMultiplier(Attribute attr, Type type, int slotNumber, int level) {
		if(this.attr == attr) {
			if(this.type == type) {
				return getTypeMultiplier(level);
			}
			else return getAttrMultiplier(level);
		}else return 1;
	}
	
	public float getAttrMultiplier(int level) {
		if(level>=attrActive) {
			return attrMultipliers.get(level-attrActive);
		} else return 1;
	}
	
	public float getTypeMultiplier(int level) {
		if(level>=typeActive) {
			return typeMultipliers.get(level-typeActive);
		} else return 1;
	}
	
	public float getSlotMultipler(Medal medal, int slotNumber, int level) {
		return getSlotMultiplier(medal.attribute, medal.type, slotNumber, level);
	}
	
}
