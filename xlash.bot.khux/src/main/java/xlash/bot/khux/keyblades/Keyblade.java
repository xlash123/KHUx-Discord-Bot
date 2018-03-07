package xlash.bot.khux.keyblades;

public class Keyblade {
	
	public String name;
	/**
	 * The list of all the slots in the keyblade. Slot 5 is friend
	 */
	public Slot[] slots;
	
	public Keyblade(String name, Slot[] slots) {
		this.name = name;
		this.slots = slots;
	}
	
	public Slot getSlot(int number) {
		if(number<slots.length) {
			return slots[number];
		}
		return null;
	}

}
