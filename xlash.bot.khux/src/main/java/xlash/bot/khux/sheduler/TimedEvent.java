package xlash.bot.khux.sheduler;

/**
 * An event that runs every x minutes
 *
 */
public abstract class TimedEvent {

	public final String name;
	public int frequency;
	public boolean enabled;
	
	public long lastRun;
	
	/**
	 * Create an event that runs every x minutes
	 * @param name Unique name
	 * @param enabled Determines if the event will run
	 * @param frequency in minutes
	 */
	public TimedEvent(String name, boolean enabled, int frequency){
		this.name = name;
		this.frequency = frequency;
		this.enabled = enabled;
		lastRun = 0;
	}
	
	/**
	 * Gets the frequency of this event in minutes
	 * @return
	 */
	public int getFrequency(){
		return frequency;
	}
	
	public abstract void run();
	
	public final String getName(){
		return name;
	}

}
