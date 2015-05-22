package com.knocksfornometer.mapimage.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

	/**
	 * Load String to String Map from a JSON file
	 */
	public static Map<String, String> loadStringMapFromJsonFile(String filePath) throws FileNotFoundException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		Reader reader = new FileReader(filePath);
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		return gson.fromJson(reader, type);
	}
	/**
	 * Loads the JSON data and closes the Reader.
	 */
	public static <T> T fromJson(Reader reader, Class<T> classOfT, String dateFormat) throws IOException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat(dateFormat);
		Gson gson = gsonBuilder.create();
		T jsonDataObject;
		try{
			jsonDataObject = gson.fromJson(reader, classOfT);
		}finally{
			if(reader != null)
				reader.close();
		}
		return jsonDataObject;
	}
}
