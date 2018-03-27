package edu.carleton.comp4601.users;

import java.util.ArrayList;

import Jama.Matrix;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;

public class UserProfile {
	
	public final static int NUM_GENRES = 28;
	String username;
	ArrayList<String> moviesReviewed;
	double[][] moviesVector;
	
	public UserProfile() {
		moviesVector = new double[1][NUM_GENRES];
	}
	
	
	
	/** Action Adult Adventure Animation Biography Comedy Crime Documentary Drama Family Fantasy Film-Noir Game-Show History Horror Musical Music Mystery News Reality-TV Romance Sci-Fi Short Sport Talk-Show Thriller War Western**/

	
	public Matrix getUserVector() {
		return new Matrix(moviesVector);
	}
	
	
}
