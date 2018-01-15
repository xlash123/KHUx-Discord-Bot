package xlash.bot.khux;

/**
 * The type of game: North America (NA) or Japan (JP)
 *
 */
public enum GameEnum {
	
	NA,
	JP;
	
	/**
	 * Takes a string and converts it into the GameState
	 * @param game The nickname of the game
	 * @return The corresponding GameState, or NA if none match.
	 */
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
		case "sena":
			return NA;
		case "sejp":
			return JP;
			default:
				return NA;
		}
	}

}
