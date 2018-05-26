package xlash.bot.khux.sheduler;

import xlash.bot.khux.GameEnum;

/**
 * An event that runs at a specific time
 *
 */
public abstract class Event {
	
	public final String name;
	public String[] times;
	public boolean enabled;
	public GameEnum game;
	
	/**
	 * Add an event to run at a specific time.
	 * @param name Unique name for the event
	 * @param enabled Determines if the event will run
	 * @param game Used to automatically determine time zone
	 * @param times The times that the event runs in the time zone of the given game (PT for NA, JT or JP): hh:mm:ss (must include initial '0' to fill all fields), e.g. 09:00:00 instead of 9:00:00
	 */
	public Event(String name, boolean enabled, GameEnum game, String... times){
		this.name = name;
		this.times = times;
		this.enabled = enabled;
		this.game = game;
	}
	
	public final String getName(){
		return name;
	}
	
	/**
	 * Gets the times at which this event should run
	 * @return
	 */
	public final String[] getTimes(){
		return times;
	}
	
	/**
	 * Called when the getTime() is equal to the current time.
	 */
	public abstract void run(String time);
	
}
