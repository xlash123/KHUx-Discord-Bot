package xlash.bot.khux.keyblades;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import xlash.bot.khux.medals.Attribute;

public class KeybladeDeserilizer implements JsonDeserializer<Keyblade>{

	@Override
	/**
	 * Converts Json into a Java Keyblade object
	 */
	public Keyblade deserialize(JsonElement json, Type typeAsT, JsonDeserializationContext context) throws JsonParseException {
		String name;
		Slot[] slots = new Slot[6];
		JsonObject keyblade = json.getAsJsonObject();
		name = keyblade.get("name").getAsString();
		for(int s=1; s<slots.length+1; s++) {
			JsonObject slot = keyblade.getAsJsonObject("slot"+s);
			//Parameters for slot
			Attribute slotAttribute = null;
			xlash.bot.khux.medals.Type slotType = null;
			int attrActive = 0;
			ArrayList<Float> attrMultipliers = new ArrayList<>();
			int typeActive = 0;
			ArrayList<Float> typeMultipliers = new ArrayList<>();
			int slotActive = slot.get("active").getAsInt();
			
			JsonObject friend = slot.getAsJsonObject("friend");
			if(friend!=null) {
				//We'll just use typeMultipliers since the function in slot will end up using this if the attribute and type are null
				parseForLevel(friend, typeMultipliers);
			}else {
				JsonObject attr = slot.getAsJsonObject("attr");
				attrActive = attr.get("active").getAsInt();
				slotAttribute = Attribute.getFromName(attr.get("name").getAsString());
				parseForLevel(attr, attrMultipliers);
				JsonObject type = slot.getAsJsonObject("type");
				typeActive = type.get("active").getAsInt();
				JsonElement typeNameEl = type.get("name");
				if(typeNameEl.isJsonNull()) return null;
				slotType = xlash.bot.khux.medals.Type.getFromName(typeNameEl.getAsString());
				parseForLevel(type, typeMultipliers);
			}
			slots[s-1] = new Slot(slotAttribute, slotType, attrActive, typeActive, slotActive, attrMultipliers, typeMultipliers);
		}
		return new Keyblade(name, slots);
	}
	
	/**
	 * Reads through the given object as an object or an array as necessary to determine multipliers at each level.
	 * @param o
	 * @param multipliers
	 */
	private void parseForLevel(JsonObject o, ArrayList<Float> multipliers) {
		if(o.get("level").isJsonArray()) {
			JsonArray levels = o.getAsJsonArray("level");
			for(int i=0; i<levels.size(); i++) {
				multipliers.add(levels.get(i).getAsFloat());
			}
		}else {
			JsonObject levels = o.getAsJsonObject("level");
			//Just making sure I read all levels.
			for (int i = 0; i < 100; i++) {
				if(levels.has(""+i)) multipliers.add(levels.get(""+i).getAsFloat());
			}
		}
	}

}
