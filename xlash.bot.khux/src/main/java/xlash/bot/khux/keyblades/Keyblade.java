package xlash.bot.khux.keyblades;

public class Keyblade {
	
	/**
	 * The name of the Keyblade
	 */
	public final String name;
	/**
	 * The list of all the slots in the keyblade. Slot 5 is friend
	 */
	public final Slot[] slots;
	
	/**
	 * The maximum level this Keyblade can be.
	 */
	private int maxLevel;
	
	/**
	 * An object representing a Keyblade
	 * @param name
	 * @param slots
	 */
	public Keyblade(String name, Slot[] slots) {
		this.name = name;
		this.slots = slots;
		
		for(Slot slot : slots) {
			if(slot.attrActive==0) {
				this.maxLevel = slot.attrMultipliers.size()-1;
				break;
			}
		}
	}
	
	/**
	 * Returns the maximum level this Keyblade can be.
	 * @return
	 */
	public int getMaxLevel() {
		return maxLevel;
	}
	
	/**
	 * Gets the slot of specified number.
	 * @param number
	 * @return The specified slot for this Keyblade
	 */
	public Slot getSlot(int number) {
		if(number<slots.length) {
			return slots[number];
		}
		return null;
	}

}
