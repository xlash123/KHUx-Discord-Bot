package xlash.bot.khux.sheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Schedules things
 *
 */
public class Scheduler {
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");
	
	public volatile ArrayList<Event> events = new ArrayList<Event>();
	
	public volatile ArrayList<TimedEvent> timedEvents = new ArrayList<TimedEvent>();
	
	public final Thread schedulerThread;
	
	/**
	 * Initializes a scheduler.
	 */
	public Scheduler(){
		SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
		schedulerThread = new Thread("Scheduler"){
			@Override
			public void run(){
				scheduler();
				System.err.println("Scheduler failed.");
			}
		};
	}
	
	public void startThread(){
		this.schedulerThread.start();
	}
	
	public void stopThread(){
		this.schedulerThread.interrupt();
	}
	
	/**
	 * Add an timed event
	 * @param event
	 */
	public void addTimedEvent(TimedEvent event){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(event.getName())){
				System.err.println("An event with name " + e.getName() + " already exists. Event not added.");
				return;
			}
		}
		this.timedEvents.add(event);
	}
	/**
	 * Remove a timed event
	 * @param name
	 */
	public void removeTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				timedEvents.remove(e);
				return;
			}
		}
	}
	
	/**
	 * Add an event
	 * @param event
	 */
	public void addEvent(Event event){
		for(Event e : events){
			if(e.getName().equals(event.getName())){
				System.err.println("An event with name " + e.getName() + " already exists. Event not added.");
				return;
			}
		}
		this.events.add(event);
		System.out.println("Added enabled event " + event.getName());
	}
	
	/**
	 * Remove an event
	 * @param name
	 */
	public void removeEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				events.remove(e);
				return;
			}
		}
	}
	
	/**
	 * Enable an event
	 * @param name
	 */
	public void enableEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				e.enabled = true;
				System.out.println("Enabled event " + name);
				return;
			}
		}
		System.out.println("Failed to enabled event " + name);
	}
	
	/**
	 * Disable an event
	 * @param name
	 */
	public void disableEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				e.enabled = false;
				System.out.println("Disabled event " + name);
				return;
			}
		}
		System.out.println("Failed to disable event " + name);
	}
	
	/**
	 * Enable a timed event
	 * @param name
	 */
	public void enableTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				e.enabled = true;
				System.out.println("Enabled timed event " + name);
				return;
			}
		}
		System.out.println("Failed to enabled timed event " + name);
	}
	
	/**
	 * Disable a timed event
	 * @param name
	 */
	public void disableTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				e.enabled = false;
				System.out.println("Disabled timed event " + name);
				return;
			}
		}
		System.out.println("Failed to disable timed event " + name);
	}
	
	/**
	 * Runs the scheduler to do things at the events' specified times
	 */
	private void scheduler(){
		System.out.println("Starting scheduler");
		long timeSec = System.currentTimeMillis()/1000;
		long prevTimeSec = new Long(timeSec);
		
		while(true){
			timeSec = System.currentTimeMillis()/1000;
			
			//Safety just in case 2 or more seconds pass in one tick
			if(timeSec-prevTimeSec > 1){
				System.err.println("Scheduler is behind by " + (timeSec-prevTimeSec) + " seconds");
				for(int i=1; prevTimeSec+i<timeSec; i++){
					this.executeEvents(prevTimeSec+i);
				}
				System.err.println("System has caught up.");
			}
			
			prevTimeSec = new Long(timeSec);
			
			synchronized(this.events){
				this.executeEvents(timeSec);
			}
			
			while(timeSec==System.currentTimeMillis()/1000){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Executes any events that should run at the specified UNIX time
	 * @param timeSec in UNIX time (milliseconds)
	 */
	private void executeEvents(long timeSec){
		String currentTime = getGMTTime(timeSec);
		Date currentDate = convert(currentTime);
		
		for(Event e : events){
			for(String time : e.getTimes()){
				if(e.enabled && time.equals(currentTime)){
					System.out.println("Running " + e.getName());
					try{
						e.run();
						System.out.println("Passed " + e.getName());
					}catch(Exception e1){
						System.err.println("Error occured while running event: " + e.getName());
						e1.printStackTrace();
					}
					break;
				}
			}
		}
		
		for(TimedEvent e : timedEvents){
			if(e.enabled){
				long difference = difference(convert(e.lastRun), currentDate).getTime();
				if(difference > e.getFrequency()*60000){
					System.out.println("Running " + e.getName());
					try{
						e.run();
					}catch(Exception e1){
						System.err.println("Error occured while running timed event: " + e.getName());
						e1.printStackTrace();
					}
					e.lastRun = String.valueOf(currentTime);
				}
			}
		}
	}
	
	/**
	 * Gets the GMT or UTC time.
	 * @param format The format the time should be represented.
	 * @return Time/Date in the GMT time zone.
	 */
	public static String getGMTTime(String format) {
		final Date currentTime = new Date();

		final SimpleDateFormat sdf = new SimpleDateFormat(format);

		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(currentTime);
	}

	/**
	 * Gets the time in the GMT time zone in the 00:00:00 to 23:59:59 standard.
	 * @return Time in the 00:00:00 to 23:59:59 standard.
	 */
	public static String getGMTTime() {
		return getGMTTime("HH:mm:ss");
	}
	
	public static String getGMTTime(long sec){
		return SDF.format(new Date(sec*1000));
	}
	
	/**
	 * Converts the given string into a Date object.
	 * @param time in format HH:mm:ss
	 * @return Date object representing the current time (ignoring actual day).
	 */
	public static Date convert(String time){
		try {
			return SDF.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the difference in time. Should not exceed a difference of 24 hours.
	 * @param before
	 * @param after
	 * @return A Date object representing the time difference.
	 */
	public Date difference(Date before, Date after){
		boolean nextDay = before.after(after);
		long timeDiff;
		if(nextDay){
			timeDiff = after.getTime() - before.getTime() + (24*60*60*1000);
		}else{
			timeDiff = after.getTime() - before.getTime();
		}
		return new Date(timeDiff);
	}
}
