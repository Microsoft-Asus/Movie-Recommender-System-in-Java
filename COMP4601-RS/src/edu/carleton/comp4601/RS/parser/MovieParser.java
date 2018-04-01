package edu.carleton.comp4601.RS.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.util.movie.MovieSearcher;

public class MovieParser {
	static final String moviePath = "/Users/julianclayton/Documents/workspace/COMP4601-A2/COMP4601-RS/data/pages";
	Document doc;

	public static void main(String[] args) {
		File[] movieFiles = getMovieFiles();
		ArrayList<String> movieIds = parseIds(movieFiles);
		for (int i = 0; i <  movieIds.size();i++){
			try {		
				Document doc = Jsoup.connect(buildUrl(movieIds.get(i))).get();
				String title = parseTitle(doc);
				List<String> genres = MovieSearcher.searchForMovie(title);
				String review = getAllReviewsText(Jsoup.parse(movieFiles[i], "UTF-8"));
				System.out.println(review);
				if (genres != null) {
					DatabaseManager.getInstance().addMovieToDb(movieIds.get(i), title, review, genres);
				}
				Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
			}
		}	
	}
	public static void initMovies() {
		File[] movieFiles = getMovieFiles();
		ArrayList<String> movieIds = parseIds(movieFiles);
		for (int i = 0; i <  movieIds.size();i++){
			try {		
				Document doc = Jsoup.connect(buildUrl(movieIds.get(i))).get();
				String title = parseTitle(doc);
				List<String> genres = MovieSearcher.searchForMovie(title);
				String review = getAllReviewsText(Jsoup.parse(movieFiles[i], "UTF-8"));
				System.out.println(review);
				if (genres != null) {
					DatabaseManager.getInstance().addMovieToDb(movieIds.get(i), title, review, genres);
				}
				Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
			}
		}	
	}
	private static String parseTitle(Document doc){
		String t = "";
		Elements titleElement = doc.getElementsByTag("title");
		t = titleElement.toString();
		t = t.substring(35, t.lastIndexOf('<'));
		t = t.replaceAll("\\[.*]|\\(.*\\)","");
		
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
	private static String getAllReviewsText(Document document) {
		Elements elements = document.select("p");
		String allReviewTextForMovie = "";
		for (Element e : elements) {
			allReviewTextForMovie += e.text();
		}
		return allReviewTextForMovie;
	}
	
	private static File[] getMovieFiles(){
		File folder = new File(moviePath);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}

}
