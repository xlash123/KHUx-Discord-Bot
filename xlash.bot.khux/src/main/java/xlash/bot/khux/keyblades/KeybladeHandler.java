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
	
	public KeybladeHandler() {
		this.keyblades = new ArrayList<>();
		updateKeybladeData();
	}
	
	/**
	 * Updates all the Keyblade settings from khuxtracker
	 */
	public void updateKeybladeData() {
		this.keyblades.clear();
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
			System.out.println("# of Keyblades: " + keybladesJson.size());
			for(int i=0; i<keybladesJson.size(); i++) {
				JsonElement keybladeJson = keybladesJson.get(i);
				Keyblade parsedKeyblade = gson.fromJson(keybladeJson, Keyblade.class);
				System.out.println("Created " + parsedKeyblade.name);
				keyblades.add(parsedKeyblade);
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

}
