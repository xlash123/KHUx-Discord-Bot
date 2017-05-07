package xlash.bot.khux;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.btobastian.javacord.entities.message.Message;

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

	public void refreshMedalList() {
		disabled = true;
		nicknames.clear();
		medalNamesAndLink.clear();
		getMedalList();
		createNicknames();
		disabled = false;
	}

	public void resetDescriptions() {
		medalDescriptions.clear();
	}

	public void getMedalList() {
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

	public void createNicknames() {
		naNicknames();
		jpNicknames();
	}

	public void naNicknames() {
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

	public void jpNicknames() {
		for (String name : this.jpMedalNamesAndLink.keySet()) {
			String original = name.substring(0, name.length());
			name.replace("Ver", "");
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
			name = name.replace("Medal", "");
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
			jpNicknames.put(name, original);
		}
		jpNicknames.put("Tieri", "Illustrated KH II Kairi");
		jpNicknames.put("Pooglet", "Pooh & Piglet");
		jpNicknames.put("BronzeDonald", "Donald A");
	}

	public void getMedalInfo(String realName, Message message, GameEnum game) {
		if (game.equals(GameEnum.NA) && this.medalDescriptions.containsKey(realName)) {
			message.reply(this.medalDescriptions.get(realName));
			return;
		} else if (this.jpMedalDescriptions.containsKey(realName)) {
			message.reply(this.jpMedalDescriptions.get(realName));
			return;
		}
		String website = "http://www.khunchainedx.com/wiki/Donald_A";
		if (game.equals(GameEnum.NA)) {
			this.medalNamesAndLink.get(realName);
		} else
			this.jpMedalNamesAndLink.get(realName);

		Document doc;
		try {
			URL url = new URL(website);
			doc = Jsoup.parse(url.openStream(), null, "");
			// TODO Make compatible with non-6* versions
			Elements medalMaxInfo = doc.getElementById("mw-content-text").getElementsByAttributeValueStarting("title", "6").get(0).getElementsByTag("td");
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
			message.reply(reply);
			if (game.equals(GameEnum.NA)) {
				this.medalDescriptions.put(realName, reply);
			} else
				this.jpMedalDescriptions.put(realName, reply);
		} catch (Exception e) {
			e.printStackTrace();
			message.reply("Oh dear... something went wrong... Please contact the bot creator at"
					+ " https://github.com/xlash123/KHUx-Discord-Bot/issues and link the error "
					+ "log in the console.");
		}
	}

	public String getRealNameByNickname(String name, GameEnum game) {
		if (game.equals(GameEnum.NA)) {
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
		return null;
	}

	public boolean isDisabled() {
		return disabled;
	}

}
