package xlash.bot.khux.sheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import xlash.bot.khux.GameEnum;

/**
 * Schedules things, like how often Tweets should be grabbed
 *
 */
public class Scheduler {
	
	public static final SimpleDateFormat SDF_NA = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat SDF_JP = new SimpleDateFormat("HH:mm:ss");
	
	public volatile ArrayList<Event> events = new ArrayList<Event>();
	
	public volatile ArrayList<TimedEvent> timedEvents = new ArrayList<TimedEvent>();
	
	public final Thread schedulerThread;
	
	/**
	 * Initializes a scheduler.
	 */
	public Scheduler(){
		SDF_NA.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		SDF_JP.setTimeZone(TimeZone.getTimeZone("Japan"));
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
	
	public Event getEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				return e;
			}
		}
		return null;
	}
	
	public TimedEvent getTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				return e;
			}
		}
		return null;
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
		Date currentDate = new Date(timeSec * 1000);
		String currentTimeNA = SDF_NA.format(currentDate);
		String currentTimeJP = SDF_JP.format(currentDate);
		
		for(Event e : events){
			for(String time : e.getTimes()){
				String currentTime;
				if(e.game == GameEnum.NA) {
					currentTime = currentTimeNA;
				}
				else currentTime = currentTimeJP;
				if(e.enabled && time.equals(currentTime)){
					System.out.println("Running " + e.getName());
					try{
						e.run(currentTime);
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
				long difference = currentDate.getTime() - e.lastRun;
				if(difference > e.getFrequency()*60000){
					System.out.println("Running " + e.getName());
					try{
						e.run();
					}catch(Exception e1){
						System.err.println("Error occured while running timed event: " + e.getName());
						e1.printStackTrace();
					}
					e.lastRun = currentDate.getTime();
				}
			}
		}
	}
}
