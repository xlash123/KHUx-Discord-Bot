package xlash.bot.khux.medals;

import java.util.ArrayList;

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
