package xlash.bot.khux.medals;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MedalDeserializer implements JsonDeserializer<Medal> {

	@Override
	public Medal deserialize(JsonElement el, Type type, JsonDeserializationContext context) throws JsonParseException {
		Gson gson = new Gson();
		JsonObject ob = el.getAsJsonObject();
		System.out.println(ob);
		RawMedal baseMedal = gson.fromJson(ob.get("medal"), RawMedal.class);
		ob = ob.getAsJsonObject("bank2");
		//If a medal rarity has a supernova, add that supernova. If its unlocked form also has supernova, add that data too (str and supernova)
		RawMedal m6 = null, m7 = null, unlk = null;
		Supernova m7S = null, unlkS = null;
		if(ob.has("6")) {
			JsonObject medalBase = get(get(get(ob, 6), 0), 0).getAsJsonObject();
//			if(size(get(ob, 6)) > 1) { //Has a supernova
//				gson.fromJson(ob.getAsJsonArray("6").get(1).getAsJsonArray().get(0).getAsJsonObject(), RawSupernova.class);
//			}
			m6 = gson.fromJson(medalBase, RawMedal.class);
		}
		if(ob.has("7")) {
			JsonObject medalBase = get(get(get(ob, 7), 0), 0).getAsJsonObject();
			if(size(get(ob, 7)) > 1) { //Has a supernova
				JsonElement defaultSupernova = get(get(get(ob, 7), 1), 0);
				if(defaultSupernova != null) m7S = gson.fromJson(defaultSupernova, RawMedal.class).toSupernova();
				if(get(get(get(ob, 7), 1), 1) != null) { //Has unlockable form
					JsonObject unlocked = medalBase.deepCopy();
					JsonObject unlockedSupernova = get(get(get(ob, 7), 1), 1).getAsJsonObject();
					unlkS = gson.fromJson(unlockedSupernova, RawMedal.class).toSupernova();
					unlocked.add("strength", unlockedSupernova.get("strength"));
					unlocked.add("strength_min", unlockedSupernova.get("strength_min"));
					unlk = gson.fromJson(unlocked, RawMedal.class);
				}
			}
			m7 = gson.fromJson(medalBase, RawMedal.class);
		}
		return new Medal(baseMedal, m6, m7, unlk, m7S, unlkS);
	}
	
	private JsonElement get(JsonElement e, int i) {
		if(e == null) return null;
		else if(e.isJsonObject()) {
			return e.getAsJsonObject().get("" + i);
		}else if(e.isJsonArray()) {
			return e.getAsJsonArray().get(i);
		}
		System.out.println("You stupid");
		return null;
	}
	
	private int size(JsonElement e) {
		if(e == null) return 0;
		else if(e.isJsonObject()) {
			return e.getAsJsonObject().size();
		}else if(e.isJsonArray()){
			return e.getAsJsonArray().size();
		}
		return 0;
	}

}
