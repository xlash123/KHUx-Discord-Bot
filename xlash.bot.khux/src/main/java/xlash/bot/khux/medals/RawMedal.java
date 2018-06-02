package xlash.bot.khux.medals;

/**
 * An intermediate class to convert the JSON from khuxtracker to the Medal class
 *
 */
public class RawMedal {
	
	public String mid, name, special, img;
	public float min_low_damage, min_high_damage, max_low_damage, max_high_damage;
	public int tier, strength, strength_min, gauges, attribute, type, aoe;
	
	public MedalDetails toMedal() {
		return new MedalDetails(mid, name, special, type, attribute, tier, min_low_damage, min_high_damage, max_low_damage, max_high_damage, strength, gauges, aoe, img);
	}

}
