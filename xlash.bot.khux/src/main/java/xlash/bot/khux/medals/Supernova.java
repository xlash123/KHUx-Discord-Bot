package xlash.bot.khux.medals;

public class Supernova {
	
	public String special;
	public float baseLow, baseHigh, maxLow, maxHigh;
	public int gauges, hits;
	public Target target;
	
	public Supernova(String special, float min_low_damage, float min_high_damage, float max_low_damage, float max_high_damage, int gauges_additional, int hits, int aoe) {
		this.special = special;
		this.baseLow = min_low_damage;
		this.baseHigh = min_high_damage;
		this.maxHigh = max_low_damage;
		this.maxLow = max_high_damage;
		this.gauges = gauges_additional;
		this.hits = hits;
		this.target = Target.getFromId(aoe);
	}

}
