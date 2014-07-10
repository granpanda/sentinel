package gp.e3.sentinel.infrastructure.utils;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
	
	public static Gson getDefaultGson() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter());

		return gsonBuilder.create();
	}
}