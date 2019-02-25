package xlash.bot.khux.medals;

public class Medal {
	
	public MedalDetails sixStar, sevenStar, unlocked;
	public String mid, name;
	
	public Medal(RawMedal baseMedal, RawMedal six, RawMedal seven, RawMedal unlocked, Supernova snNorm, Supernova snPlus) {
		name = baseMedal.name;
		if(six != null) {
			this.mid = six.mid;
			this.sixStar = new MedalDetails(baseMedal.mid, baseMedal.name, six.special, baseMedal.type, baseMedal.attribute, baseMedal.tier, six.min_low_damage, six.min_high_damage, six.max_low_damage, six.max_high_damage, six.strength, six.gauges, six.gauges_additional, baseMedal.aoe, baseMedal.img, null);
		}
		if(seven != null) {
			if(this.mid==null) this.mid = seven.mid;
			if(seven.strength == 0) {
				seven.strength = (int) (((0.6d*seven.strength_min + 1900d)/39d)*(Math.pow(119, 0.6)) + seven.strength_min);
			}
			this.sevenStar = new MedalDetails(baseMedal.mid, baseMedal.name, seven.special, baseMedal.type, baseMedal.attribute, baseMedal.tier, seven.min_low_damage, seven.min_high_damage, seven.max_low_damage, seven.max_high_damage, seven.strength, seven.gauges, seven.gauges_additional, baseMedal.aoe, baseMedal.img, snNorm);
			if(unlocked != null) {
				this.unlocked = new MedalDetails(baseMedal.mid, baseMedal.name, unlocked.special, baseMedal.type, baseMedal.attribute, baseMedal.tier, unlocked.min_low_damage, unlocked.min_high_damage, unlocked.max_low_damage, unlocked.max_high_damage, unlocked.strength, unlocked.gauges, unlocked.gauges_additional, baseMedal.aoe, baseMedal.img, snPlus);
			}
		}
		if(six == null && seven == null){
			System.err.println("There exists no form of this medal.");
			return;
		}
	}
	
	public boolean hasSeven() {
		return sevenStar != null;
	}
	
	public boolean hasSix() {
		return sixStar != null;
	}
	
	public boolean hasSupernova() {
		return (hasSeven() ? sevenStar.supernova != null : false) || (isUnlockable() ? unlocked.supernova != null : false);
	}
	
	public boolean isUnlockable() {
		return unlocked != null;
	}
	
	public MedalDetails getSix() {
		return sixStar;
	}
	
	public MedalDetails getSeven() {
		return sevenStar;
	}
	
	public MedalDetails getUnlocked() {
		return unlocked;
	}

}
