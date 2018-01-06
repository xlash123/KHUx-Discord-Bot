package xlash.bot.khux.medals;

public class Medal {
	
	public String mid, name, special;
	public Type type;
	public Attribute attribute;
	public Tier tier;
	public float baseLow, baseHigh, maxLow, maxHigh;
	public int strength, gauges;
	public Target target;
	
	public Medal(String mid, String name, String special, int type, int attribute, int tier, float baseLow, float baseHigh, float maxLow, float maxHigh, int strength, int gauges, int target) {
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
		this.target = Target.getFromId(target);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Medal) {
			Medal m = (Medal) o;
			return m.mid.equals(this.mid);
		}
		return false;
	}

}
