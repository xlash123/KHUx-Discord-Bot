package xlash.bot.khux.medals;

public enum Type {
	
	UPRIGHT("Upright", 0),
	REVERSED("Revered", 1);
	
	public String name;
	public int id;
	
	private Type(String name, int id) {
		this.name= name;
		this.id = id;
	}
	
	public static Type getFromId(int id) {
		for(Type t : Type.class.getEnumConstants()) {
			if(t.id == id) {
				return t;
			}
		}
		return null;
	}

}
