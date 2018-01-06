package xlash.bot.khux.medals;

public class RawMedal {
	
	public String mid, name, special;
	public float min_low_damage, min_high_damage, max_low_damage, max_high_damage;
	public int tier, strength, gauges, attribute, type, aoe;
	
	public Medal toMedal() {
		return new Medal(mid, name, special, type, attribute, tier, min_low_damage, min_high_damage, max_low_damage, max_high_damage, strength, gauges, aoe);
	}

}
