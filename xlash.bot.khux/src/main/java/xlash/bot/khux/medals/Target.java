package xlash.bot.khux.medals;

/**
 * The type of target the medal targets: single, AOE, or random
 *
 */
public enum Target {
	
	SINGLE("Single", 0),
	AOE("All", 1),
	RANDOM("Random", 2);
	
	public String name;
	
	/**
	 * The id as it appears on khuxtracker
	 */
	public int id;
	
	private Target(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public static Target getFromId(int id) {
		for(Target t : Target.class.getEnumConstants()) {
			if(t.id == id) {
				return t;
			}
		}
		return null;
	}

}
