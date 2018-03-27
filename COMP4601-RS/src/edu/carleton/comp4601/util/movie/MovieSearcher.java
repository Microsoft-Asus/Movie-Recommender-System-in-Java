package edu.carleton.comp4601.util.movie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MovieSearcher {

	
	static String apiKey = "215f6954";
	private final static String BASE_URL = "http://www.omdbapi.com"; 
	private final static String TITLE_QUERY = "/?t="; 
	private final static String API_KEY_PARAM = "&apikey=";
	static String genre;
	
	public static List<String> searchForMovie(String title) throws IOException {
			title = title.replaceAll(" ", "+");
		    URL url = new URL(BASE_URL + TITLE_QUERY + title + API_KEY_PARAM + apiKey);
		    HttpURLConnection request = (HttpURLConnection) url.openConnection();
		    request.connect();
		    JsonParser jp = new JsonParser(); 
		    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); 
		    JsonObject rootobj = root.getAsJsonObject();  
		    genre = rootobj.get("Genre").getAsString();
		    List<String> genres =Arrays.asList(genre.split("\\s*,\\s*"));
		    return genres;
	}
	public static void main(String[] args) throws IOException {
		List<String> genres = searchForMovie("Guardians of the Galaxy");
		for (String genre : genres) {
			System.out.println(genre);
		}
	}
	
}
