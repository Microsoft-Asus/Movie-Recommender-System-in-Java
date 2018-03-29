package edu.carleton.comp4601.users;

import java.util.ArrayList;

import Jama.Matrix;
import edu.carleton.comp4601.dictonary.DictionaryAndFeatureGenerator;
import edu.carleton.comp4601.lucene.Lucene;
import edu.carleton.comp4601.model.GenreReviews;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;

public class UserProfile {
	
	String username;
	ArrayList<String> moviesReviewed;
	double[] features;
	private int cluster;
	
	public UserProfile(String username) {
		this.username = username;
		features = new double[DictionaryAndFeatureGenerator.GENRES.length];
	}
	public UserProfile(String username, ArrayList<Double> features) {
		this.username = username;
		this.features = new double[DictionaryAndFeatureGenerator.GENRES.length];
		for (int i = 0; i < features.size(); i++) {
			this.features[i] = features.get(i);
		}
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
		features[DictionaryAndFeatureGenerator.indexOfGenre(genre)] = accum/dict.size();
	}
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	public int getCluster() {
		return cluster;
	}
	
	/** Action Adult Adventure Animation Biography Comedy Crime Documentary Drama Family Fantasy Film-Noir Game-Show History Horror Musical Music Mystery News Reality-TV Romance Sci-Fi Short Sport Talk-Show Thriller War Western**/

	

	
}
