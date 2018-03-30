package edu.carleton.comp4601.users;

import java.util.ArrayList;

import Jama.Matrix;
import edu.carleton.comp4601.generators.DictionaryAndFeatureGenerator;
import edu.carleton.comp4601.lucene.Lucene;
import net.sf.javaml.utils.ArrayUtils;


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
	private  double[] sortArray(double[] arr) {
	    for (int i = 0; i <arr.length-1; i++) {
	        for (int j = 1; j <arr.length-i; j++) {
	            if(arr[j-1]>arr[j]) {
	                double temp = arr[j-1];
	                arr[j-1] = arr[j];
	                arr[j] = temp;
	            }
	        }
	    }
	    return arr;
	}
	public int getArrayIndex(double[] arr,double value) {
	    for(int i=0;i<arr.length;i++)
	        if(arr[i]==value) return i;
	    return -1;
	}
	public double[] getFeatures() {
		return features;
	}
	public double[] getNewFeatures() {
		double[] featuresSorted = sortArray(features.clone());
		ArrayUtils.reverse(featuresSorted);
		double[] newfeatures = new double[features.length];
		
		for (int i = 0; i < featuresSorted.length; i++) {
			newfeatures[getArrayIndex(features, featuresSorted[i])] = i;
		}
		return newfeatures;
	}
	public Matrix getNewFeaturesAsMatrix() {
		double[] newfeatures = getNewFeatures();
		Matrix featuresMat  = new Matrix(new double[1][newfeatures.length]);
		featuresMat.getArray()[0] = newfeatures;
		return featuresMat;

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
	public String getFavouriteGenre() {
		double largestVal = 0;
		int index = 0;
		for (int i = 0; i < features.length; i++) {
			if (largestVal < features[i]) {
				largestVal = features[i];
				index = i;
			}
		}
		return DictionaryAndFeatureGenerator.GENRES[index];
	}
	
	/** Action Adult Adventure Animation Biography Comedy Crime Documentary Drama Family Fantasy Film-Noir Game-Show History Horror Musical Music Mystery News Reality-TV Romance Sci-Fi Short Sport Talk-Show Thriller War Western**/

	

	
}
