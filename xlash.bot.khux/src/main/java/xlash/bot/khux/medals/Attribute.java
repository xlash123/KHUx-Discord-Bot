package xlash.bot.khux.medals;

public enum Attribute {
	
	POWER("Power", 1),
	SPEED("Speed", 2),
	MAGIC("Magic", 3);
	
	public String name;
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

}
