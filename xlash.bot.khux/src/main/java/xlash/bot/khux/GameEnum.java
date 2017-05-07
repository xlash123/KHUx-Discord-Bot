package xlash.bot.khux;

public enum GameEnum {
	
	NA(1),
	JP(5);
	
	public int tab;
	
	private GameEnum(int tab){
		this.tab = tab;
	}
	
	public static GameEnum parseString(String game){
		if(game==null) return NA;
		switch(game.toLowerCase()){
		case "global":
			return NA;
		case "na":
			return NA;
		case "jp":
			return JP;
		case "japan":
			return JP;
		case "north america":
			return NA;
			default:
				return NA;
		}
	}

}
