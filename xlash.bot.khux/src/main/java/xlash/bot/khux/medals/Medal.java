package xlash.bot.khux.medals;

public class Medal {
	
	public MedalDetails sixStar, sevenStar;
	public String mid;
	
	public Medal(RawMedal six, RawMedal seven) {
		this.mid = six.mid;
		this.sixStar = new MedalDetails(six.mid, six.name, six.special, six.type, six.attribute, six.tier, six.min_low_damage, six.min_high_damage, six.max_low_damage, six.max_high_damage, six.strength, six.gauges, six.aoe, six.img);
		if(seven.strength == 0) {
			seven.strength = (int) (((0.6d*seven.strength_min + 1900d)/39d)*(Math.pow(119, 0.6)) + seven.strength_min);
		}
		this.sevenStar = seven != null ? new MedalDetails(seven.mid, seven.name, seven.special, seven.type, seven.attribute, seven.tier, seven.min_low_damage, seven.min_high_damage, seven.max_low_damage, seven.max_high_damage, seven.strength, seven.gauges, seven.aoe, seven.img.equals("null") ? six.img : seven.img) : null;
	}
	
	public boolean hasSeven() {
		return sevenStar != null;
	}
	
	public MedalDetails getSix() {
		return sixStar;
	}
	
	public MedalDetails getSeven() {
		return sevenStar;
	}

}
