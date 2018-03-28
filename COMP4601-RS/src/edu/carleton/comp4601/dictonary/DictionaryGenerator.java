package edu.carleton.comp4601.dictonary;

import java.util.ArrayList;
import java.util.HashMap;

import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.model.Movie;

public class DictionaryGenerator {

	private final String[] GENRES = {"Drama", "Horror", "Crime", "Sci-Fi", "Action", "Fantasy", "Adventure", "Biography", "History", "Mystery", "Comedy", "Family"};
	private HashMap<String, String> genreReviewMap;
	private ArrayList<Movie> movies; 
	
	private static DictionaryGenerator instance;
	
	
	public DictionaryGenerator() {
		genreReviewMap = new HashMap<String,String>();
		instance = this;
		
	}
	
	/*
	 * Initiates banks of words for each genre in the database
	 */
	private void initiateWordBanks() {
		for (int i = 0; i < GENRES.length; i++){
			genreReviewMap.put(GENRES[i], "");
		}
		movies = DatabaseManager.getInstance().loadMovies();
		for (Movie movie : movies) {
			for (String genre : movie.getGenres()) {
				if (genreReviewMap.containsKey(genre)) {
					String reviews = genreReviewMap.get(genre);
					genreReviewMap.put(genre, reviews + movie.getReviews());
				}
			}
		}
		DatabaseManager.getInstance().writeReviewsToDb(genreReviewMap);
	}
	
	public static DictionaryGenerator getInstance() {
		if (instance == null)
			instance = new DictionaryGenerator();
		return instance;
	}
	
	public static void main(String[] args) {
		DictionaryGenerator dg = DictionaryGenerator.getInstance();
		dg.initiateWordBanks();
	}
}
