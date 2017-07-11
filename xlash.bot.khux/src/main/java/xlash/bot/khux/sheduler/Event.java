package xlash.bot.khux.sheduler;

/**
 * An event that runs at a specific time
 *
 */
public abstract class Event {
	
	public final String name;
	public String[] times;
	public boolean enabled;
	
	/**
	 * Add an event to run at a specific time.
	 * @param name Unique name for the event
	 * @param enabled Determines if the event will run
	 * @param times The times that the event runs in UTC: hh:mm:ss (must include initial '0' to fill all fields)
	 */
	public Event(String name, boolean enabled, String... times){
		this.name = name;
		this.times = times;
		this.enabled = enabled;
	}
	
	public final String getName(){
		return name;
	}
	
	public final String[] getTimes(){
		return times;
	}
	
	/**
	 * Called when the getTime() is equal to the current time.
	 */
	public abstract void run();
	
}
