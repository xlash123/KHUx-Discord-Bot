package xlash.bot.khux.medals;

public class Medal {
	
	public MedalDetails sixStar, sevenStar;
	public String mid;
	
	public Medal(RawMedal six, RawMedal seven) {
		this.mid = six.mid;
		this.sixStar = new MedalDetails(six.mid, six.name, six.special, six.type, six.attribute, six.tier, six.min_low_damage, six.min_high_damage, six.max_low_damage, six.max_high_damage, six.strength, six.gauges, six.aoe, six.img);
		this.sevenStar = new MedalDetails(seven.mid, seven.name, seven.special, seven.type, seven.attribute, seven.tier, seven.min_low_damage, seven.min_high_damage, seven.max_low_damage, seven.max_high_damage, seven.strength, seven.gauges, seven.aoe, six.img);;
	}
	
	public MedalDetails getSix() {
		return sixStar;
	}
	
	public MedalDetails getSeven() {
		return sevenStar;
	}

}
