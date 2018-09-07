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
		JsonObject ob = el.getAsJsonObject();
		Gson gson = new Gson();
		System.out.println(ob);
		return new Medal(ob.has("6") ? gson.fromJson(ob.get("6"), RawMedal.class) : null, ob.has("7") ? gson.fromJson(ob.get("7"), RawMedal.class) : null);
	}

}
