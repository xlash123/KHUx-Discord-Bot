package xlash.bot.khux.sheduler;

public abstract class TimedEvent {
	
	public String lastRun;
	
	public TimedEvent(){
		lastRun = "00:00:00";
	}
	
	/**
	 * Gets the frequency of this event in minutes
	 * @return
	 */
	public abstract int getFrequency();
	
	public abstract void run();
	
	public abstract String getName();

}
