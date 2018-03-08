package xlash.bot.khux.keyblades;

public class Keyblade {
	
	public final String name;
	/**
	 * The list of all the slots in the keyblade. Slot 5 is friend
	 */
	public final Slot[] slots;
	
	private int maxLevel;
	
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
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public Slot getSlot(int number) {
		if(number<slots.length) {
			return slots[number];
		}
		return null;
	}

}
