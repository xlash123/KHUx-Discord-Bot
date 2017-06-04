package xlash.bot.khux;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MedalHandler {

	public HashMap<String, String> nicknames = new HashMap<String, String>();
	public HashMap<String, String> medalNamesAndLink = new HashMap<String, String>();
	public HashMap<String, String> medalDescriptions = new HashMap<String, String>();

	public HashMap<String, String> jpNicknames = new HashMap<String, String>();
	public HashMap<String, String> jpMedalNamesAndLink = new HashMap<String, String>();
	public HashMap<String, String> jpMedalDescriptions = new HashMap<String, String>();

	public volatile boolean disabled;

	public MedalHandler() {
		getMedalList();
		System.out.println("Got medal list");
		createNicknames();
		System.out.println("Created nicknames");
	}

	/**
	 * Deletes all medals in the database and regrabs the current list for both games. Preserves medal descriptions.
	 */
	public void refreshMedalList() {
		disabled = true;
		nicknames.clear();
		jpNicknames.clear();
		medalNamesAndLink.clear();
		jpMedalNamesAndLink.clear();
		getMedalList();
		createNicknames();
		disabled = false;
	}

	/**
	 * Resets the medal descriptions for both games in the off chance something was changed.
	 */
	public void resetDescriptions() {
		medalDescriptions.clear();
		jpMedalDescriptions.clear();
	}

	private void getMedalList() {
		try {
			Document doc = Jsoup.connect("http://www.khunchainedx.com/wiki/Medal").get();
			Elements medalList = doc.getElementById("mw-content-text").getElementsByClass("collapsible collapsed")
					.get(0).getElementsByTag("tr");
			for (int i = 1; i < medalList.size(); i++) {
				Elements medalLinks = medalList.get(i).getElementsByTag("a");
				for (Element link : medalLinks) {
					this.medalNamesAndLink.put(link.attr("title"), link.absUrl("href").replaceAll("%26", "&"));
				}
			}
			Elements jpMedalList = doc.getElementById("mw-content-text").getElementsByClass("collapsible collapsed")
					.get(1).getElementsByTag("tr");
			for (int i = 1; i < jpMedalList.size(); i++) {
				Elements medalLinks = jpMedalList.get(i).getElementsByTag("a");
				for (Element link : medalLinks) {
					String title = link.attr("title").replace("(", "").replace(")", "");
					if (!title.contains("page does not exist")) {
						this.jpMedalNamesAndLink.put(link.attr("title"), link.absUrl("href").replaceAll("%26", "&"));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates all the nicknames for each medal in both games.
	 */
	public void createNicknames() {
		while(disabled){}
		naNicknames();
		jpNicknames();
	}

	private void naNicknames() {
		for (String name : this.medalNamesAndLink.keySet()) {
			String original = name.substring(0, name.length());
			if (name.contains("é")) {
				name = name.replace("é", "e");
				nicknames.put(name, original);
			}
			if (name.contains("è")) {
				name = name.replace("è", "e");
				nicknames.put(name, original);
			}
			name = name.replace("KH II ", "KH2");
			name = name.replace("The ", "");
			name = name.replace("WORLD OF FF", "WOFF");
			name = name.replace("Timeless River", "TR");
			name = name.replace("Halloween", "HT");
			name = name.replace("Atlantica", "AT");
			name = name.replace("Key Art ", "KA");
			name = name.replace("(Medal)", "");
			if (name.contains("Illustrated")) {
				name = name.replace("Illustrated", "i");
				if (name.split(" ").length > 1) {
					String[] words = name.split(" ");
					if (name.contains("&")) {
						String product = "";
						boolean skip = name.split("&").length > 2;
						for (String word : words) {
							if (skip && word.equals("&"))
								continue;
							product += word.substring(0, 1);
						}
						name = product;
					}
				}
			} else if (name.contains("&")) {
				String[] words = name.split(" ");
				String product = "";
				boolean skip = name.split("&").length > 2;
				for (String word : words) {
					if (skip && word.equals("&"))
						continue;
					product += word.substring(0, 1);
				}
				name = product;
			}

			name = name.replace(" ", "");
			nicknames.put(name, original);
		}
		nicknames.put("Tieri", "Illustrated KH II Kairi");
		nicknames.put("Pooglet", "Pooh & Piglet");
		nicknames.put("BronzeDonald", "Donald A");
	}

	private void jpNicknames() {
		for (String name : this.jpMedalNamesAndLink.keySet()) {
			String original = name.substring(0, name.length());
			name = name.replace("Ver", "");
			name = name.replace("(EX)", "EX");
			if (name.contains("é")) {
				name = name.replace("é", "e");
				jpNicknames.put(name, original);
			}
			if (name.contains("è")) {
				name = name.replace("è", "e");
				jpNicknames.put(name, original);
			}
			name = name.replace("KH II ", "KH2");
			name = name.replace("The ", "");
			name = name.replace("WORLD OF FF", "WOFF");
			name = name.replace("Timeless River", "TR");
			name = name.replace("Halloween", "HT");
			name = name.replace("Atlantica", "AT");
			name = name.replace("Key Art ", "KA");
			name = name.replace("(Medal)", "");
			if (name.contains("Illustrated")) {
				name = name.replace("Illustrated", "i");
				if (name.split(" ").length > 1) {
					String[] words = name.split(" ");
					for(int i=0; i<words.length; i++){
						if(i>0 && words[i].equalsIgnoreCase("i")){
							for(int ii=i-1; ii>=0; ii--){
								words[ii+1] = words[ii];
							}
							words[0] = "i";
							String product = "";
							for(String word : words){
								product += word;
							}
							name = product;
						}
					}
					if (name.contains("&")) {
						String product = "";
						boolean skip = name.split("&").length > 2;
						for (String word : words) {
							if (skip && word.equals("&"))
								continue;
							product += word.substring(0, 1);
						}
						name = product;
					}
				}
			} else if (name.contains("&")) {
				String[] words = name.split(" ");
				String product = "";
				boolean skip = name.split("&").length > 2;
				for (String word : words) {
					if (skip && word.equals("&"))
						continue;
					product += word.substring(0, 1);
				}
				name = product;
			}

			name = name.replace(" ", "");
			jpNicknames.put(name, original);
		}
		jpNicknames.put("Tieri", "Illustrated KH II Kairi");
		jpNicknames.put("Pooglet", "Pooh & Piglet");
		jpNicknames.put("BronzeDonald", "Donald A");
	}

	/**
	 * Grabs the medal data from the wiki page.
	 * @param realName The name of the medal as it appears in the database.
	 * @param game The game to search on
	 * @return The String meant to be published on Discord.
	 */
	public String getMedalInfo(String realName, GameEnum game) {
		while(disabled){}
		System.out.println("Getting info for game " + game.toString());
		if (game==GameEnum.NA && this.medalDescriptions.containsKey(realName)) {
			return this.medalDescriptions.get(realName);
		}
		if (game==GameEnum.JP && this.jpMedalDescriptions.containsKey(realName)) {
			return this.jpMedalDescriptions.get(realName);
		}
		String website;
		if (game==GameEnum.NA) {
			website = this.medalNamesAndLink.get(realName);
		} else website = this.jpMedalNamesAndLink.get(realName);
		
		if(website==null || website.isEmpty()) website = "http://www.khunchainedx.com/wiki/Donald_A";
		
		Document doc;
		try {
			URL url = new URL(website);
			doc = Jsoup.parse(url.openStream(), null, "");
			// TODO Make compatible with non-6* versions
			Elements medalMaxInfoB = doc.getElementById("mw-content-text").getElementsByTag("div");
			Elements medalMaxInfo;
			if(medalMaxInfoB.size() > 5){
				try{
				medalMaxInfo = medalMaxInfoB.get(game.tab)
						.getElementsByAttributeValueStarting("title", "6").get(0).getElementsByTag("td");
				}catch (IndexOutOfBoundsException e){
					medalMaxInfo = medalMaxInfoB.get(1)
							.getElementsByAttributeValueStarting("title", "6").get(0).getElementsByTag("td");
				}
			}else medalMaxInfo = medalMaxInfoB.get(1)
					.getElementsByAttributeValueStarting("title", "6").get(0).getElementsByTag("td");
			Elements attributes = new Elements();
			for (int i = 0; i < medalMaxInfo.size(); i++) {
				if (!medalMaxInfo.get(i).hasAttr("colspan"))
					attributes.add(medalMaxInfo.get(i));
				if (i > 1 && medalMaxInfo.get(i).getElementsByAttributeValueMatching("colspan", "4").size() > 0)
					attributes.add(medalMaxInfo.get(i));
			}

			String medalAttribute = attributes.get(9).text();
			String medalStrength = attributes.get(10).text();
			String medalDefense = attributes.get(11).text();
			String medalAbility = attributes.get(13).text();
			String medalTarget = attributes.get(18).text();
			String medalTier = attributes.get(19).text();
			String medalMultiplier = attributes.get(20).text();
			String medalGuages = attributes.get(21).text();

			String reply = "======== \n" + realName + ": \n" + "Attribute: " + medalAttribute + " \n" + "Ability: "
					+ medalAbility + " \n" + "Str/Def + " + medalStrength + "/" + medalDefense + " \n" + "Target: "
					+ medalTarget + " \n" + "Tier " + medalTier + " \n" + "Multiplier: " + medalMultiplier + " \n"
					+ "Cost: " + medalGuages + " SP \n" + "========";
			if (game==GameEnum.NA) {
				this.medalDescriptions.put(realName, reply);
			} else this.jpMedalDescriptions.put(realName, reply);
			return reply;
		} catch (Exception e) {
			e.printStackTrace();
			return "Oh dear... something went wrong...";
		}
	}

	/**
	 * Attempts to get the real name of the medal. After checking the databases for exact matches, it will
	 * then search by words and return the highest probable medal above 70%(inclusive) match.
	 * @param name The nickname or real name of the medal to verify.
	 * @param game The game to search
	 * @return The medal's real name, or null if none is found
	 */
	public String getRealNameByNickname(String name, GameEnum game) {
		while(disabled){}
		name = name.toLowerCase();
		if (game==GameEnum.NA) {
			for (String test : this.medalNamesAndLink.keySet()) {
				if (test.equalsIgnoreCase(name))
					return test;
			}
			for (String test : this.nicknames.keySet()) {
				if (test.equalsIgnoreCase(name.replace(" ", "")))
					return nicknames.get(test);
			}
		} else {
			for (String test : this.jpMedalNamesAndLink.keySet()) {
				if (test.equalsIgnoreCase(name))
					return test;
			}
			for (String test : this.jpNicknames.keySet()) {
				if (test.equalsIgnoreCase(name.replace(" ", "")))
					return jpNicknames.get(test);
			}
		}
		//Now we search with words to get a most likely candidate.
		String[] words = name.split(" ");
		if(game==GameEnum.NA){
			HashMap<String, Float> percentMatch = new HashMap<String, Float>();
			for(String test : this.medalNamesAndLink.keySet()){
				int matchLength = 0;
				String compare = test.toLowerCase();
				String[] testWords = compare.split(" ");
				HashMap<String, Integer> repeats = new HashMap<String, Integer>();
				for(String w : words){
					int numOfOcc = 0;
					if(repeats.get(w) != null) numOfOcc = repeats.get(w);
					int initOcc = numOfOcc;
					for(String tw : testWords){
						if(tw.equals(w)){
							numOfOcc--;
							if(numOfOcc<0){
								matchLength += tw.length();
								repeats.put(w, initOcc+1);
								break;
							}
						}
					}
				}
				float higher = Math.max(test.length()-testWords.length, name.length()-words.length);
				percentMatch.put(test, matchLength/higher);
			}
			Iterator<String> names = percentMatch.keySet().iterator();
			Iterator<Float> percents = percentMatch.values().iterator();
			String winner = null;
			float winnerPer = 0;
			for(int i=0; i<percentMatch.size(); i++){
				String currentName = names.next();
				float currentPercent = percents.next();
				System.out.println(currentName + " : " + currentPercent);
				if(currentPercent == 1){
					return currentName;
				}
				if(currentPercent > winnerPer && currentPercent >= .7f){
					winner = currentName;
					winnerPer = currentPercent;
				}
			}
			System.out.println();
			System.out.println(winner + " " + winnerPer);
			return winner;
		} else {
			HashMap<String, Float> percentMatch = new HashMap<String, Float>();
			for(String test : this.jpMedalNamesAndLink.keySet()){
				int matchLength = 0;
				String compare = test.toLowerCase();
				String[] testWords = compare.split(" ");
				HashMap<String, Integer> repeats = new HashMap<String, Integer>();
				for(String w : words){
					int numOfOcc = 0;
					if(repeats.get(w) != null) numOfOcc = repeats.get(w);
					int initOcc = numOfOcc;
					for(String tw : testWords){
						if(tw.equals(w)){
							numOfOcc--;
							if(numOfOcc<0){
								matchLength += tw.length();
								repeats.put(w, initOcc+1);
								break;
							}
						}
					}
				}
				float higher = Math.max(test.length()-testWords.length, name.length()-words.length);
				percentMatch.put(test, matchLength/higher);
			}
			Iterator<String> names = percentMatch.keySet().iterator();
			Iterator<Float> percents = percentMatch.values().iterator();
			String winner = null;
			float winnerPer = 0;
			for(int i=0; i<percentMatch.size(); i++){
				String currentName = names.next();
				float currentPercent = percents.next();
				System.out.println(currentName + " : " + currentPercent);
				if(currentPercent == 1){
					return currentName;
				}
				if(currentPercent > winnerPer && currentPercent >= .7f){
					winner = currentName;
					winnerPer = currentPercent;
				}
			}
			System.out.println();
			System.out.println(winner + " " + winnerPer);
			return winner;
		}
	}

	/**
	 * Whether or not the medal handler is disabled.
	 * @return True when the database is refreshing.
	 */
	public boolean isDisabled() {
		return disabled;
	}

}
