package edu.carleton.comp4601.dictonary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.lucene.Lucene;
import edu.carleton.comp4601.model.Dictionary;
import edu.carleton.comp4601.model.GenreReviews;
import edu.carleton.comp4601.model.GenreReviews.GenreReview;
import edu.carleton.comp4601.model.Movie;
import edu.carleton.comp4601.users.UserProfile;

public class DictionaryGenerator {

	public static final String[] GENRES = {"Drama", "Horror", "Crime", "Sci-Fi", "Action", "Fantasy", "Adventure", "Biography", "History", "Mystery", "Comedy", "Family"};
	private HashMap<String, String> genreReviewMap;
	private ArrayList<Movie> movies; 
	private List<String> stopwords;
	
	private static DictionaryGenerator instance;
	
	
	public DictionaryGenerator() {
		try {
			genreReviewMap = new HashMap<String,String>();
			stopwords = new ArrayList<String>();
			stopwords = Files.readAllLines(Paths.get("dict/stopwords.txt"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	private void generateDictionaryForGenres() {
		System.out.println("Starting Dictionary creation...");
		DatabaseManager dbm = DatabaseManager.getInstance();
		dbm.loadReviews();
		ArrayList<GenreReview> reviews = GenreReviews.getInstance().getReviews();
		for (GenreReview review : reviews) {
			ArrayList<String> topWords = countWordsInString(review.getReviews());
			dbm.addDictionaryToDb(topWords, review);
		}
        System.out.print("Dictionaries Generated");

		
	}
	public ArrayList<String> countWordsInString(String s) {
        String sentence = s;
        ArrayList<String> words = new ArrayList<String>();
        Stream<String> wordStream = Pattern.compile("\\W").splitAsStream(sentence);
        HashMap<String,Integer> unsortedMap = new HashMap<String,Integer>();
        wordStream.forEach((wordReal) -> {
            String word = wordReal.toLowerCase();
            if (!word.equals("") && !stopwords.contains(word)) {
                if (unsortedMap.get(word) == null) {
                    unsortedMap.put(word, 0);
                }
                unsortedMap.put(word, unsortedMap.get(word) + 1);
            }
        });
        Map<String, Integer> sortedMap =
             unsortedMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue((v1,v2)->v2.compareTo(v1)))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
                                      (e1, e2) -> e1, LinkedHashMap::new));

        int i = 0;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            if (i == 1000) break;
        	i++;
            words.add(entry.getKey());
            //System.out.println(entry.getKey() + " " + entry.getValue());

        }
        return words;
    }
	public void generateFeaturesForUsers() {
		System.out.println("Generating Features");
		DatabaseManager dbm = DatabaseManager.getInstance();
		ArrayList<String> usernames = Lucene.getInstance().getUsers();
		ArrayList<Dictionary> dictionaries = dbm.loadDictionariesFromDb();
		for (String username: usernames) {
			System.out.println("Generating features for: " + username);
			UserProfile user = new UserProfile(username);
			for (Dictionary dict : dictionaries) {
				System.out.println("For dictionary: " + dict.getGenre());
				user.generateFeature(dict.getDictionary(), dict.getGenre());
			}
			System.out.println("Saving " + user.getUsername() + " to db");
			dbm.saveUserProfileToDatabase(user);
		}
	}
	public static DictionaryGenerator getInstance() {
		if (instance == null)
			instance = new DictionaryGenerator();
		return instance;
	}
	public static int indexOfGenre(String genre) {
		int index = 0;
		for (int i = 0; i < GENRES.length; i++) {
			if (GENRES[i].equals(genre)) {
				index = i;
			}
		}
		return index;
	}
	public static void main(String[] args) {
		DictionaryGenerator dg = DictionaryGenerator.getInstance();
		//dg.generateDictionaryForGenres();
		dg.generateFeaturesForUsers();
	}
}
