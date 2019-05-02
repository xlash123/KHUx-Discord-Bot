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
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class KeybladeHandler {
	
	/**
	 * A list of all the Keyblades in the game
	 */
	public ArrayList<Keyblade> keyblades;
	/**
	 * A list used to convert the integer level into the decimal level.
	 */
	public ArrayList<Float> levelAliases;
	
	/**
	 * Handles anything and everything to do with Keyblade data
	 */
	public KeybladeHandler() {
		this.keyblades = new ArrayList<>();
		this.levelAliases = new ArrayList<>();
		updateKeybladeData();
	}
	
	/**
	 * Updates all the Keyblade settings from khuxtracker
	 */
	public synchronized void updateKeybladeData() {
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
				try {
					JsonElement keybladeJson = keybladesJson.get(i);
					Keyblade parsedKeyblade = gson.fromJson(keybladeJson, Keyblade.class);
					if(parsedKeyblade != null) {
						System.out.println("Created " + parsedKeyblade.name);
						keyblades.add(parsedKeyblade);
					}else System.out.println("Failed to create a Keyblade");
				}catch(JsonParseException e) {
					System.out.println("Failed to create a Keyblade");
				}
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
			this.levelAliases.clear();
			for(int i=0; i<levelAliasesArray.size(); i++) {
				this.levelAliases.add(levelAliasesArray.get(i).getAsFloat());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the Keyblade with the specified name
	 * @param name
	 * @return The Keyblade, or null if not found
	 */
	public Keyblade getKeyblade(String name) {
		for(Keyblade keyblade : keyblades) {
			if(keyblade.name.equalsIgnoreCase(name)) {
				return keyblade;
			}
		}
		return null;
	}
	
	/**
	 * Gets the Keyblade of specified id, e.g. 0 -> Starlight
	 * @param id
	 * @return
	 */
	public Keyblade getKeyblade(int id) {
		return this.keyblades.get(id);
	}
	
	/**
	 * Returns the integer level given the user visible level
	 * @param level
	 * @return The integer level
	 */
	public int getRealLevel(float level) {
		for(int i=0; i<levelAliases.size(); i++) {
			if(levelAliases.get(i)==level) {
				return i;
			}
		}
		return levelAliases.size()-1;
	}
	
	/**
	 * Returns the user visible level given its integer level
	 * @param level
	 * @return The user visible level
	 */
	public float getAliasLevel(int level) {
		if(level >= 0 && level < levelAliases.size()) {
			return levelAliases.get(level);
		}
		return levelAliases.get(levelAliases.size()-1);
	}

}
