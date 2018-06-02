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
		return new Medal(gson.fromJson(ob.get("6"), RawMedal.class), ob.has("7") ? gson.fromJson(ob.get("7"), RawMedal.class) : null);
	}

}
