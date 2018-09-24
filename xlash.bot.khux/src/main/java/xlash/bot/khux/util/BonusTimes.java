package xlash.bot.khux.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import xlash.bot.khux.GameEnum;
import xlash.bot.khux.sheduler.Scheduler;

/**
 * A list of all the bonus times, plus methods for various time calculations
 *
 */
public class BonusTimes {
	
	public static String[] doubleLuxStartNA = new String[] {"02:00:00", "08:00:00", "14:00:00", "20:00:00"};
	public static String[] doubleLuxStartJP = new String[] {"12:00:00", "22:00:00"};
	public static String[] doubleLuxStopNA = new String[] {"03:00:00", "09:00:00", "15:00:00", "21:00:00"};
	public static String[] doubleLuxStopJP = new String[] {"13:00:00", "23:00:00"};
	
	public static String[] uxBonusStartNA = new String[] {"04:00:00", "10:00:00", "14:00:00", "19:00:00", "23:00:00"};
	public static String[] uxBonusEndNA = new String[] {"04:30:00", "10:30:00", "14:30:00", "19:30:00", "23:30:00"};
	public static String[] uxBonusStartJP = new String[] {"19:00:00", "23:00:00"};
	public static String[] uxBonusEndJP = new String[] {"19:30:00", "23:30:00"};
	
	/**
	 * Returns the unlocalized time string into a human-friendly version
	 * @param time
	 * @return
	 */
	public static String getTimeLocalized(String time) {
		SimpleDateFormat parser = Scheduler.SDF_NA;
		SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
		formatter.setTimeZone(parser.getTimeZone());
		try {
			return formatter.format(parser.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
	
	/**
	 * Gets the time difference to the next lux boost for the specified game.
	 * @param game
	 * @return
	 */
	public static int luxTimeDifference(GameEnum game) {
		return luxTimeDifference(game, null);
	}
	
	/**
	 * Gets the time difference in minutes for the next double lux time.
	 * @param game
	 * @return time difference in minutes
	 */
	public static int luxTimeDifference(GameEnum game, boolean[] times) {
		String[] start;
		if(game.equals(GameEnum.NA)) {
			start = doubleLuxStartNA;
		}else {
			start = doubleLuxStartJP;
		}
		
		if(times != null) {
			ArrayList<String> ret = new ArrayList<>();
			for(int i=0; i<start.length && i<times.length; i++) {
				if(times[i]) ret.add(start[i]);
			}
			return timeDifference(game, ret.toArray(new String[ret.size()]));
		}
		
		return timeDifference(game, start);
	}
	
	/**
	 * Gets the time difference to the next Union Cross boost for the specified game.
	 * @param game
	 * @return
	 */
	public static int uxTimeDifference(GameEnum game) {
		return uxTimeDifference(game, null);
	}
	
	/**
	 * Gets the time difference in minutes for the next Union Cross bonus time.
	 * @param game
	 * @return time difference in minutes
	 */
	public static int uxTimeDifference(GameEnum game, boolean[] times) {
		String[] start;
		if(game.equals(GameEnum.NA)) {
			start = uxBonusStartNA;
		}else {
			start = uxBonusStartJP;
		}
		
		if(times != null) {
			ArrayList<String> ret = new ArrayList<>();
			for(int i=0; i<start.length && i<times.length; i++) {
				if(times[i]) ret.add(start[i]);
			}
			return timeDifference(game, ret.toArray(new String[ret.size()]));
		}
		
		return timeDifference(game, start);
	}
	
	/**
	 * The main method used for determining the difference in times.
	 * @param game Used for time zones
	 * @param times The list of times used to determine the time difference
	 * @return The number of minutes until the closest specified time
	 */
	private static int timeDifference(GameEnum game, String[] times) {
		SimpleDateFormat sdf;
		if(game.equals(GameEnum.NA)) {
			sdf = Scheduler.SDF_NA;
		}else {
			sdf = Scheduler.SDF_JP;
		}
		
		int closest = Integer.MAX_VALUE;
		Calendar now = Calendar.getInstance(sdf.getTimeZone());
		for(String time : times) {
			try {
				Date luxTime = sdf.parse(time);
				Calendar cal = Calendar.getInstance(sdf.getTimeZone());
				cal.clear();
				cal.setTimeInMillis(luxTime.getTime());
				cal.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
				int difference = (int) ((cal.getTimeInMillis() - now.getTimeInMillis())/1000+60);
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
	
	/**
	 * Determines if currentTime exists in times
	 * @param currentTime
	 * @param times
	 * @return
	 */
	public static boolean hasTime(String currentTime, String[] times) {
		for(String time : times) {
			if(time.equals(currentTime)) return true;
		}
		return false;
	}
	
	/**
	 * Returns all the parts of times that line up with a "true" in active. Used for lux/uc selections.
	 * @param active
	 * @param times
	 * @return
	 */
	public static ArrayList<String> getTimes(boolean[] active, String[] times) {
		ArrayList<String> ret = new ArrayList<>();
		for(int i=0; i<active.length; i++) {
			if(active[i]) ret.add(times[i]);
		}
		return ret;
	}
	
}
