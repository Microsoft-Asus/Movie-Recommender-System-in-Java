package edu.carleton.comp4601.dictonary;

import edu.carleton.comp4601.RS.db.DatabaseManager;

public class DictionaryGenerator {

	private final String[] GENRES = {"Drama", "Horror", "Crime", "Sci-Fi", "Action", "Fantasy", "Adventure", "Biography", "History", "Mystery", "Comedy", "Family"};
	
	private static DictionaryGenerator instance;
	
	
	public DictionaryGenerator() {
		instance = this;
		
	}
	
	private void initiateWordBanks() {
		
	}
	
	public static DictionaryGenerator getInstance() {
		if (instance == null)
			instance = new DictionaryGenerator();
		return instance;
	}
}
