package edu.carleton.comp4601.users;

import java.util.ArrayList;

import Jama.Matrix;
import edu.carleton.comp4601.dictonary.DictionaryGenerator;
import edu.carleton.comp4601.lucene.Lucene;
import edu.carleton.comp4601.model.GenreReviews;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;

public class UserProfile {
	
	public final static int NUM_GENRES = 28;
	String username;
	ArrayList<String> moviesReviewed;
	double[] features;
	
	public UserProfile(String username) {
		this.username = username;
		System.out.println("Size: " + DictionaryGenerator.GENRES.length);
		features = new double[DictionaryGenerator.GENRES.length];
	}
	public String getUsername() {
		return username;
	}
	public double[] getFeatures() {
		return features;
	}
	public void generateFeature(ArrayList<String> dict, String genre) {
		float accum = 0;
		for (String term : dict) {
			float termtfidf = Lucene.getInstance().query(username, term);
			accum += termtfidf*100;
		}
		System.out.println("Saving to index: " + DictionaryGenerator.indexOfGenre(genre) + "for genre " + genre );
		features[DictionaryGenerator.indexOfGenre(genre)] = accum/dict.size();
	}
	
	
	/** Action Adult Adventure Animation Biography Comedy Crime Documentary Drama Family Fantasy Film-Noir Game-Show History Horror Musical Music Mystery News Reality-TV Romance Sci-Fi Short Sport Talk-Show Thriller War Western**/

	

	
}
