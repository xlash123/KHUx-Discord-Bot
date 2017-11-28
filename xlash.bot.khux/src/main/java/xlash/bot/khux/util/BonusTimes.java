package xlash.bot.khux.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.sheduler.Scheduler;

public class BonusTimes {
	
	public static String[] doubleLuxStartNA = new String[] {"02:00:00", "08:00:00", "14:00:00", "20:00:00"};
	public static String[] doubleLuxStartJP = new String[] {"12:00:00", "22:00:00"};
	public static String[] doubleLuxStopNA = new String[] {"03:00:00", "09:00:00", "15:00:00", "21:00:00"};
	public static String[] doubleLuxStopJP = new String[] {"13:00:00", "23:00:00"};
	
	public static String[] dailyRaidStartNA = new String[] {"12:00:00", "19:00:00"};
	public static String[] dailyRaidStopNA = new String[] {"13:00:00", "10:00:00"};
	public static String[] dailyRaidStartJP = new String[] {};
	public static String[] dailyRaidStopJP = new String[] {};
	
	public static String[] uxBonusStartNA = new String[] {"04:00:00", "10:00:00", "14:00:00", "19:00:00", "23:00:00"};
	public static String[] uxBonusEndNA = new String[] {"04:30:00", "10:30:00", "14:30:00", "19:30:00", "23:30:00"};
	public static String[] uxBonusStartJP = new String[] {"19:00:00", "23:00:00"};
	public static String[] uxBonusEndJP = new String[] {"19:30:00", "23:30:00"};
	
	/**
	 * Gets the time difference in minutes for the next double lux time.
	 * @param game
	 * @return time difference in minutes
	 */
	public static int luxTimeDifference(GameEnum game) {
		String[] start;
		if(game.equals(GameEnum.NA)) {
			start = doubleLuxStartNA;
		}else {
			start = doubleLuxStartJP;
		}
		
		return timeDifference(game, start);
	}
	
	public static int uxTimeDifference(GameEnum game) {
		String[] start;
		if(game.equals(GameEnum.NA)) {
			start = uxBonusStartNA;
		}else {
			start = uxBonusStartJP;
		}
		
		return timeDifference(game, start);
	}
	
	public static int raidTimeDifference(GameEnum game) {
		String[] start;
		if(game.equals(GameEnum.NA)) {
			start = dailyRaidStartNA;
		}else {
			start = dailyRaidStartJP;
		}
		
		return timeDifference(game, start);
	}
	
	private static int timeDifference(GameEnum game, String[] times) {
		SimpleDateFormat sdf;
		if(game.equals(GameEnum.NA)) {
			sdf = Scheduler.SDF_NA;
		}else {
			sdf = Scheduler.SDF_JP;
		}
		
		int closest = Integer.MAX_VALUE;
		Date now = new Date();
		for(String time : times) {
			try {
				Date luxTime = sdf.parse(time);
				luxTime.setYear(Calendar.getInstance().get(Calendar.YEAR)-1900);
				luxTime.setMonth(Calendar.getInstance().get(Calendar.MONTH));
				luxTime.setDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				int difference = (int) ((luxTime.getTime() - now.getTime())/1000) + 60;
				if(difference < 0) {
					difference += 86400;
				}
				if(difference < closest) {
					closest = difference;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return closest/60;
	}

}
