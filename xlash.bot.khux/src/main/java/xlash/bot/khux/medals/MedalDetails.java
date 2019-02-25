package xlash.bot.khux.medals;

/**
 * A collection of all of a medal's stats
 *
 */
public class MedalDetails {
	
	public String mid, name, special, img;
	public Type type;
	public Attribute attribute;
	public Tier tier;
	public float baseLow, baseHigh, maxLow, maxHigh;
	public int strength, gauges, gaugesAdded;
	public Target target;
	public Supernova supernova;
	
	/**
	 * An object representing a medal of specified data
	 * @param mid
	 * @param name
	 * @param special
	 * @param type
	 * @param attribute
	 * @param tier
	 * @param baseLow
	 * @param baseHigh
	 * @param maxLow
	 * @param maxHigh
	 * @param strength
	 * @param gauges
	 * @param target
	 * @param img
	 */
	public MedalDetails(String mid, String name, String special, int type, int attribute, int tier, float baseLow, float baseHigh, float maxLow, float maxHigh, int strength, int gauges, int gauges_additional, int target, String img, Supernova supernova) {
		this.mid = mid;
		this.name = name;
		this.special = special;
		this.type = Type.getFromId(type);
		this.attribute = Attribute.getFromId(attribute);
		this.tier = Tier.getFromTier(tier);
		this.baseLow = baseLow;
		this.baseHigh = baseHigh;
		this.maxLow = maxLow;
		this.maxHigh = maxHigh;
		this.strength = strength;
		this.gauges = gauges;
		this.gaugesAdded = gauges_additional;
		this.target = Target.getFromId(target);
		this.img = img;
		this.supernova = supernova;
	}
	
	public boolean equals(Object o) {
		if(o instanceof MedalDetails) {
			MedalDetails m = (MedalDetails) o;
			return m.mid.equals(this.mid);
		}
		return false;
	}
	
}
