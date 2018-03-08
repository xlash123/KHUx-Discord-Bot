package xlash.bot.khux.keyblades;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class KeybladeHandler {
	
	public ArrayList<Keyblade> keyblades;
	public ArrayList<Float> levelAliases;
	
	public KeybladeHandler() {
		this.keyblades = new ArrayList<>();
		this.levelAliases = new ArrayList<>();
		updateKeybladeData();
	}
	
	/**
	 * Updates all the Keyblade settings from khuxtracker
	 */
	public void updateKeybladeData() {
		try {
			HttpsURLConnection site = (HttpsURLConnection) new URL("https://khuxtracker.com/js/static.js").openConnection();
			StringBuilder sb = new StringBuilder();
			site.connect();
			Scanner in = new Scanner(site.getInputStream());
			while(in.hasNext()) {
				sb.append(in.next() + " ");
			}
			in.close();
			String allTheData = sb.toString();
			int index = allTheData.indexOf("[", allTheData.indexOf("var $keyblade ="))+1;
			int endIndex = 0;
			int openCount = 1;
			int closeCount = 0;
			for(int i=index; openCount!=closeCount; i++) {
				if(allTheData.charAt(i) == '[') {
					openCount++;
				}else if(allTheData.charAt(i) == ']') {
					closeCount++;
				}
				endIndex = i;
			}
			String keybladesJsonString = allTheData.substring(index-1, endIndex+1);
			JsonParser parser = new JsonParser();
			JsonArray keybladesJson = parser.parse(keybladesJsonString).getAsJsonArray();
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(Keyblade.class, new KeybladeDeserilizer());
			Gson gson = gsonBuilder.create();
			this.keyblades.clear();
			for(int i=0; i<keybladesJson.size(); i++) {
				JsonElement keybladeJson = keybladesJson.get(i);
				Keyblade parsedKeyblade = gson.fromJson(keybladeJson, Keyblade.class);
				System.out.println("Created " + parsedKeyblade.name);
				keyblades.add(parsedKeyblade);
			}
			
			//Now for the level aliases.
			index = allTheData.indexOf("{", allTheData.indexOf("var $keyblade_aliases ="))+1;
			endIndex = 0;
			openCount = 1;
			closeCount = 0;
			for(int i=index; openCount!=closeCount; i++) {
				if(allTheData.charAt(i) == '{') {
					openCount++;
				}else if(allTheData.charAt(i) == '}') {
					closeCount++;
				}
				endIndex = i;
			}
			String levelAliasesString = allTheData.substring(index-1, endIndex+1);
			JsonArray levelAliasesArray = parser.parse(levelAliasesString).getAsJsonObject().get("level").getAsJsonArray();
			for(int i=0; i<levelAliasesArray.size(); i++) {
				this.levelAliases.add(levelAliasesArray.get(i).getAsFloat());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Keyblade getKeyblade(String name) {
		for(Keyblade keyblade : keyblades) {
			if(keyblade.name.equalsIgnoreCase(name)) {
				return keyblade;
			}
		}
		return null;
	}
	
	public Keyblade getKeyblade(int id) {
		return this.keyblades.get(id);
	}
	
	public int getRealLevel(float level) {
		for(int i=0; i<levelAliases.size(); i++) {
			if(levelAliases.get(i)==level) {
				return i;
			}
		}
		return levelAliases.size()-1;
	}
	
	public float getAliasLevel(int level) {
		if(level >= 0 && level < levelAliases.size()) {
			return levelAliases.get(level);
		}
		return levelAliases.get(levelAliases.size()-1);
	}

}
