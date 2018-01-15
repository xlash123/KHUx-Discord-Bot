package xlash.bot.khux.medals;

/**
 * The guilt tier of a medal, both number and multiplier
 *
 */
public enum Tier {
	
	TIER1(1, 1.25f), TIER2(2, 1.5f), TIER3(3, 2f), TIER4(4, 2.3f), TIER5(5, 2.5f), TIER6(6, 2.8f), TIER7(7, 3f);
	
	public final int tier;
	public final float guiltMultiplier;
	
	private Tier(int tier, float guiltMultiplier) {
		this.tier = tier;
		this.guiltMultiplier = guiltMultiplier;
	}
	
	public static Tier getFromTier(int tier) {
		for(Tier t : Tier.class.getEnumConstants()) {
			if(t.tier == tier) {
				return t;
			}
		}
		return null;
	}

}
