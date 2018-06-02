package xlash.bot.khux.keyblades;

import java.util.ArrayList;

import xlash.bot.khux.medals.Attribute;
import xlash.bot.khux.medals.MedalDetails;
import xlash.bot.khux.medals.Type;

public class Slot {
	
	/**
	 * The slot's boosted attribute: power, speed, or magic
	 */
	public final Attribute attr;
	/**
	 * The slot's boosted type: upright or reversed
	 */
	public final Type type;
	/**
	 * The level at which the boost appears.
	 */
	public final int attrActive, typeActive;
	/**
	 * The level at which the slot is usable
	 */
	public final int slotActive;
	
	/**
	 * The boost multiplier at each level
	 */
	public final ArrayList<Float> attrMultipliers, typeMultipliers;

	/**
	 * An object representing the slot of a Keyblade at every level
	 * @param attr
	 * @param type
	 * @param attrActive
	 * @param typeActive
	 * @param slotActive
	 * @param attrMultipliers
	 * @param typeMultipliers
	 */
	public Slot(Attribute attr, Type type, int attrActive, int typeActive, int slotActive, ArrayList<Float> attrMultipliers, ArrayList<Float> typeMultipliers) {
		this.attr = attr;
		this.type = type;
		this.attrActive = attrActive;
		this.typeActive = typeActive;
		this.slotActive = slotActive;
		this.attrMultipliers = attrMultipliers;
		this.typeMultipliers = typeMultipliers;
	}
	
	/**
	 * Gets the multiplier that would be applied to a medal with specified data
	 * @param attr
	 * @param type
	 * @param slotNumber
	 * @param level of the Keyblade
	 * @return
	 */
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
	
	/**
	 * Gets the multiplier that would be applied to the specified medal
	 * @param medal
	 * @param slotNumber
	 * @param level of the Keyblade
	 * @return
	 */
	public float getSlotMultipler(MedalDetails medal, int slotNumber, int level) {
		return getSlotMultiplier(medal.attribute, medal.type, slotNumber, level);
	}
	
}
