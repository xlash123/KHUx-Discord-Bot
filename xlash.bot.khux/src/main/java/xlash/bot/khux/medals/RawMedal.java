package xlash.bot.khux.medals;

/**
 * An intermediate class to convert the JSON from khuxtracker to the Medal class
 *
 */
public class RawMedal {
	
	public String mid, name, special, img;
	public float min_low_damage, min_high_damage, max_low_damage, max_high_damage;
	public int tier, strength, strength_min, gauges, gauges_additional, hits, attribute, type, aoe;
	
	public Supernova toSupernova() {
		return new Supernova(special, min_low_damage, min_high_damage, max_low_damage, max_high_damage, gauges_additional, hits, aoe);
	}

}
