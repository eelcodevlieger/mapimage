package com.knocksfornometer.mapimage.utils;

import java.io.File;
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
	public static Map<String, String> loadStringMapFromJsonFile(File filePath) throws IOException {
		Gson gson = new GsonBuilder().create();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		try(Reader reader = new FileReader(filePath)){
			return gson.fromJson(reader, type);
		}
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
