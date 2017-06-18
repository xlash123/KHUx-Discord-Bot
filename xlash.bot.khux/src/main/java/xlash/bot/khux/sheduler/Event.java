package xlash.bot.khux.sheduler;

public interface Event {
	
	/**
	 * The times that this event is scheduled to run.
	 * @return Time in format HH:mm:ss
	 */
	public String[] getTimes();
	
	/**
	 * Called when the getTime() is equal to the current time.
	 */
	public void run();
	
	/**
	 * Helpful for identifying specific events.
	 * @return
	 */
	public String getName();
	
}
