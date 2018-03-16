package edu.carleton.comp4601.RS.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.RS.db.DatabaseManager;

public class MovieParser {
	static final String moviePath = "/Users/lauramcdougall/Documents/Carleton/COMP4601/Assignment2/pages";
	
	public static void main(String[] args) {
		File[] movieFiles = getMovieFiles();
		ArrayList<String> movieIds = parseIds(movieFiles);
		
		for (String m : movieIds){
			String title = parseTitle(buildUrl(m));
			DatabaseManager.getInstance().addMovieToDb(m, title);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
	
	private static String parseTitle(String url){
		Document doc;
		String t = "";
		try {
			doc = Jsoup.connect(url).get();
			Elements titleElement = doc.getElementsByTag("title");
			t = titleElement.toString();
			t = t.substring(35, t.lastIndexOf('<'));
			t = t.replaceAll("\\[.*]|\\(.*\\)","");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return t;
	}
	
	
	private static ArrayList<String> parseIds(File[] movies){
		ArrayList<String> idList = new ArrayList<String>();
		for (int i = 0; i < movies.length; i++){
			String id = movies[i].getName();
			id = id.substring(0, id.lastIndexOf('.'));
			idList.add(id);
		}
		return idList;
	}

	
	private static String buildUrl(String movieId){
		String amazonUrl = "https://www.amazon.ca/product-reviews/" + movieId;
		return amazonUrl;
	}
	
	
	private static File[] getMovieFiles(){
		File folder = new File(moviePath);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
}
