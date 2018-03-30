package edu.carleton.comp4601.users;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Jama.Matrix;
import edu.carleton.comp4601.generators.DictionaryAndFeatureGenerator;
import edu.carleton.comp4601.lucene.Lucene;
import edu.carleton.comp4601.model.Advertisement;
import edu.carleton.comp4601.model.Advertisements;
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
	private double average(double[] user) {
		double accum = 0;
		for (int i = 0; i < user.length; i++) {
			accum += user[i];
		}
		return accum / (user.length);
	}
	private double multiplyIndividualValuesAndSum(Matrix a, Matrix b) {
		double sum = 0;
		for (int i = 0; i < a.getArray()[0].length; i++) {
			sum += a.getArray()[0][i] * b.getArray()[0][i];
		}
		
		return sum;
	}
	private double similarity(double[] featureone, double[] featuretwo) {


		Matrix user1  = new Matrix(new double[1][featureone.length]);
		Matrix user2 = new Matrix (new double[1][featuretwo.length]);
		double average1 = average(featureone);
		double average2 = average(featuretwo);
		
		Matrix averageVector1 = new Matrix(1, featureone.length, average1);
		Matrix averageVector2 = new Matrix(1, featuretwo.length, average2);

		user1.getArray()[0] = featureone;
		user2.getArray()[0] = featuretwo;
		Matrix diff1 = user1.minus(averageVector1);
		Matrix diff2 = user2.minus(averageVector2);		
		double topp = multiplyIndividualValuesAndSum(diff1, diff2);
		double bottom = (diff1.normF()) * (diff2.normF());
		return topp/bottom;
	}
	public ArrayList<Advertisement> getClosestAdvertisements(int n) {
		Advertisements advertisements = Advertisements.getInstance();
		ArrayList<Double> similarities = new ArrayList<Double>();
		ArrayList<Advertisement> releventAds = new ArrayList<Advertisement>();
		for (Advertisement advertisement : advertisements.getAdvertisements()) {
			similarities.add(similarity(getNewFeatures(), advertisement.getFeatures()));
		}
		ArrayList<Double> similartiesSorted = (ArrayList<Double>) similarities.clone();
		Collections.sort(similartiesSorted);
		for (int i = similartiesSorted.size()-1; i > similartiesSorted.size()-(1+n); i--){
			releventAds.add(advertisements.getAdvertisements().get(similarities.indexOf(similartiesSorted.get(i))));
		}
		return releventAds;
	}
}
