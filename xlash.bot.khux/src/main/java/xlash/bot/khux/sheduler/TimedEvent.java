package xlash.bot.khux.sheduler;

public abstract class TimedEvent {

	public final String name;
	public int frequency;
	public boolean enabled;
	
	public String lastRun;
	
	public TimedEvent(String name, boolean enabled, int frequency){
		this.name = name;
		this.frequency = frequency;
		this.enabled = enabled;
		lastRun = "00:00:00";
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
