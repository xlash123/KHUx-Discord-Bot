package xlash.bot.khux.medals;

import java.util.ArrayList;

/**
 * A collection of the medal name and mid from khuxtracker. Used to convert from JSON and query the user.
 *
 */
public class SearchQuery {
	
	public ArrayList<MedalQuery> queries = new ArrayList<>();
	
	public int size() {
		return queries.size();
	}
	
	public class MedalQuery{
		
		public String name;
		public String mid;
		
		public MedalQuery(String name, String mid) {
			this.name = name;
			this.mid = mid;
		}
		
	}

}
