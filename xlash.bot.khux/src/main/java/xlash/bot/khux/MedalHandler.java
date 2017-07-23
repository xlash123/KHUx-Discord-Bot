package xlash.bot.khux;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MedalHandler {

	public HashMap<String, ArrayList<String>> nicknames = new HashMap<String, ArrayList<String>>();
	public HashMap<String, String> medalNamesAndLink = new HashMap<String, String>();
	public HashMap<String, String> medalDescriptions = new HashMap<String, String>();

	public HashMap<String, ArrayList<String>> jpNicknames = new HashMap<String, ArrayList<String>>();
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
		System.out.println("Clearing nicknames");
		nicknames.clear();
		System.out.println("Clearing JP Nicknames");
		jpNicknames.clear();
		System.out.println("Clearing medal names");
		medalNamesAndLink.clear();
		System.out.println("Clearing JP medal names");
		jpMedalNamesAndLink.clear();
		System.out.println("Generating medal list");
		getMedalList();
		System.out.println("Generating nicknames");
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
		naNicknames();
		jpNicknames();
	}

	private void naNicknames() {
		this.compileNicknames(this.medalNamesAndLink.keySet(), this.nicknames);
		addNicknameToList("Tieri", "Illustrated KH II Kairi", this.nicknames);
		addNicknameToList("Pooglet", "Pooh & Piglet", this.nicknames);
		addNicknameToList("BronzeDonald", "Donald A", this.nicknames);
	}

	private void jpNicknames() {
		this.compileNicknames(this.jpMedalNamesAndLink.keySet(), jpNicknames);
		addNicknameToList("Tieri", "Illustrated KH II Kairi", this.jpNicknames);
		addNicknameToList("Pooglet", "Pooh & Piglet", this.jpNicknames);
		addNicknameToList("BronzeDonald", "Donald A", this.jpNicknames);
	}
	
	public void compileNicknames(Set<String> realNames, HashMap<String, ArrayList<String>> nickList){
		for (String name : realNames) {
			ArrayList<String> toAdd = new ArrayList<String>(){
				@Override
				public boolean add(String e){
					e = e.replaceAll("\\s{2,}", " ").trim();
					return super.add(e);
				}
			};
			String original = name.substring(0, name.length());
			name = name.replace("Ver", "");
			name = name.replace("(EX)", "EX");
			if(name.contains("Art") && name.contains("EX")){
				name = name.replace("Art", "");
				toAdd.add(name);
			}
			if(name.contains("\"")){
				name = name.replace("\"", "");
				toAdd.add(name);
			}
			if (name.contains("\u00E9")) {
				name = name.replace("\u00E9", "e");
				toAdd.add(name);
			}
			if (name.contains("\u00E8")) {
				name = name.replace("\u00E8", "e");
				toAdd.add(name);
			}
			name = name.replace("KH II", "KH2");
			name = name.replace("KHII", "KH2");
			name = name.replace("KH 3", "KH3");
			name = name.replace("KH III", "KH3");
			name = name.replace("KHIII", "KH3");
			name = name.replace("The ", " ");
			name = name.replace("WORLD OF FF", "WOFF");
			name = name.replace("Timeless River", "TR");
			name = name.replace("Halloween", "H");
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
								product += word + " ";
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
					if (skip && word.equals("&") || word.isEmpty())
						continue;
					product += word.substring(0, 1);
				}
				name = product;
			}

			if(!toAdd.contains(name)) toAdd.add(name);
			nickList.put(original, toAdd);
		}
	}
	
	public void addNicknameToList(String nickname, String original, HashMap<String, ArrayList<String>> nickList){
		ArrayList<String> list = nickList.get(original);
		if(list != null && !list.contains(nickname)){
			list.add(nickname);
		}
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
			String result = getInitialTestResult(name, this.medalNamesAndLink.keySet(), nicknames);
			if(result != null) return result;
		} else {
			String result = getInitialTestResult(name, this.jpMedalNamesAndLink.keySet(), jpNicknames);
			if(result != null) return result;
		}
		//Now we search with words to get a most likely candidate.
		String[] words = name.split(" ");
		HashMap<String, Float> percentMatch = new HashMap<String, Float>();
		if(game==GameEnum.NA){
			this.putPercentMatchForWordsInList(name, words, percentMatch, this.medalNamesAndLink.keySet());
			for(ArrayList<String> list : this.nicknames.values()){
				this.putPercentMatchForWordsInList(name, words, percentMatch, list);
			}
		} else {
			this.putPercentMatchForWordsInList(name, words, percentMatch, this.jpMedalNamesAndLink.keySet());
			for(ArrayList<String> list : this.jpNicknames.values()){
				this.putPercentMatchForWordsInList(name, words, percentMatch, list);
			}
		}
		return this.getBestChance(percentMatch);
	}
	
	public String getInitialTestResult(String name, Set<String> realNames, HashMap<String, ArrayList<String>> nicknames){
		String nameNoSpace = name.replaceAll("\\s", "");
		for (String test : realNames) {
			if (test.replaceAll("\\s", "").equalsIgnoreCase(nameNoSpace))
				return test;
		}
		for (String realName : realNames) {
			for(String test : this.nicknames.get(realName)){
				if (test.replaceAll("\\s", "").equalsIgnoreCase(nameNoSpace))
					return realName;
			}
		}
		return null;
	}
	
	public void putPercentMatchForWordsInList(String name, String[] words, HashMap<String, Float> percentMatch, Collection<String> names){
		for(String test : names){
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
	}
	
	public String getBestChance(HashMap<String, Float> percentMatch){
		Iterator<String> names = percentMatch.keySet().iterator();
		Iterator<Float> percents = percentMatch.values().iterator();
		String winner = null;
		float winnerPer = 0;
		for(int i=0; i<percentMatch.size(); i++){
			String currentName = names.next();
			float currentPercent = percents.next();
			if(currentPercent == 1){
				return currentName;
			}
			if(currentPercent > winnerPer && currentPercent >= .8f){
				winner = currentName;
				winnerPer = currentPercent;
			}
		}
		return winner;
	}

	/**
	 * Whether or not the medal handler is disabled.
	 * @return True when the database is refreshing.
	 */
	public boolean isDisabled() {
		return disabled;
	}

}
