package xlash.bot.khux.sheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Scheduler {
	
	public static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");
	
	public volatile ArrayList<Event> events = new ArrayList<Event>();
	public volatile ArrayList<Event> disabledEvents = new ArrayList<Event>();
	
	public volatile ArrayList<TimedEvent> timedEvents = new ArrayList<TimedEvent>();
	public volatile ArrayList<TimedEvent> disabledTimedEvents = new ArrayList<TimedEvent>();
	
	public Scheduler(){
		SDF.setTimeZone(TimeZone.getTimeZone("GMT"));
		Thread scheduler = new Thread("Scheduler"){
			@Override
			public void run(){
				scheduler();
			}
		};
		scheduler.start();
	}
	
	public void addTimedEvent(TimedEvent event){
		for(Event e : events){
			if(e.getName().equals(event.getName())){
				System.err.println("An event with name " + e.getName() + " already exists. Event not added.");
				return;
			}
		}
		this.timedEvents.add(event);
	}
	
	public void addDisabledTimedEvent(TimedEvent event){
		for(TimedEvent e : disabledTimedEvents){
			if(e.getName().equals(event.getName())){
				System.err.println("An event with name " + e.getName() + " already exists. Event not added.");
				return;
			}
		}
		this.disabledTimedEvents.add(event);
		System.out.println("Added disabled event " + event.getName());
	}
	
	public void disableTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				timedEvents.remove(e);
				disabledTimedEvents.add(e);
				System.out.println("Disabled event " + name);
				return;
			}
		}
		System.out.println("Failed to disable event " + name);
	}
	
	public void enableTimedEvent(String name){
		for(TimedEvent e : disabledTimedEvents){
			if(e.getName().equals(name)){
				disabledTimedEvents.remove(e);
				addTimedEvent(e);
				System.out.println("Enabled event " + name);
				return;
			}
		}
		System.out.println("Failed to enabled event " + name);
	}
	
	public void removeTimedEvent(String name){
		for(TimedEvent e : timedEvents){
			if(e.getName().equals(name)){
				timedEvents.remove(e);
				return;
			}
		}
	}
	
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
	
	public void addDisabledEvent(Event event){
		for(Event e : disabledEvents){
			if(e.getName().equals(event.getName())){
				System.err.println("An event with name " + e.getName() + " already exists. Event not added.");
				return;
			}
		}
		this.disabledEvents.add(event);
		System.out.println("Added disabled event " + event.getName());
	}
	
	public void removeEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				events.remove(e);
				return;
			}
		}
	}
	
	public void disableEvent(String name){
		for(Event e : events){
			if(e.getName().equals(name)){
				events.remove(e);
				disabledEvents.add(e);
				System.out.println("Disabled event " + name);
				return;
			}
		}
		System.out.println("Failed to disable event " + name);
	}
	
	public void enableEvent(String name){
		for(Event e : disabledEvents){
			if(e.getName().equals(name)){
				disabledEvents.remove(e);
				addEvent(e);
				System.out.println("Enabled event " + name);
				return;
			}
		}
		System.out.println("Failed to enabled event " + name);
	}
	
	private void scheduler(){
		while(true){
			long timeSec = System.currentTimeMillis()/1000;
			String currentTime = getGMTTime();
			Date currentDate = convert(currentTime);
			
			for(Event e : events){
				for(String time : e.getTimes()){
					if(time.equals(currentTime)){
						System.out.println("Running " + e.getName());
						e.run();
						break;
					}
				}
			}
			
			for(TimedEvent e : timedEvents){
				long difference = difference(convert(e.lastRun), currentDate).getTime();
				if(difference > e.getFrequency()*60*1000){
					System.out.println("Running " + e.getName());
					e.run();
					e.lastRun = String.valueOf(currentTime);
				}
			}
			
			while(timeSec==System.currentTimeMillis()/1000){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
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
