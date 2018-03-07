package xlash.bot.khux.medals;

/**
 * The attribute of a medal: power, speed, or magic
 *
 */
public enum Attribute {
	
	POWER("Power", 1),
	SPEED("Speed", 2),
	MAGIC("Magic", 3);
	
	public String name;
	/**
	 * The id as it appears on khuxtracker
	 */
	public final int id;
	
	private Attribute(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	public static Attribute getFromId(int id) {
		for(Attribute a : Attribute.class.getEnumConstants()) {
			if(a.id == id) {
				return a;
			}
		}
		return null;
	}
	
	public static Attribute getFromName(String name) {
		for(Attribute a : Attribute.class.getEnumConstants()) {
			if(a.name.equalsIgnoreCase(name)) {
				return a;
			}
		}
		return null;
	}

}
