package edu.carleton.comp4601.model;

import java.util.Arrays;

import edu.carleton.comp4601.generators.DictionaryAndFeatureGenerator;

public class Advertisement {
	private final static int ADVERTISEMENT_ACCURACY = 3;
	
	private String html;
	private String title;
	private double[] features;
	private String[] genres;
	private String id;
	
	public Advertisement(double[] features) {
		this.features = features;
		this.genres = extractGenres(features, ADVERTISEMENT_ACCURACY);
		this.html = generateHTML();
	}
	
	private String generateHTML() {
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title>Advertisement</title></head>");
		htmlBuilder.append("<body><h1>");
		for (int i = 0; i < genres.length; i++) {
			htmlBuilder.append(genres[i] + " ");
		}
		htmlBuilder.append("</h1>");
		htmlBuilder.append("</html>");
		return htmlBuilder.toString();
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public double[] getFeatures() {
		return features;
	}
	public void setFeatures(double[] features) {
		this.features = features;
	}
	private static int getArrayIndex(double[] arr,double value) {
	    for(int i=0;i<arr.length;i++)
	        if(arr[i]==value) return i;
	    return -1;
	}
	private static int[] getLowestIndexes(int n, double[] features) {
		double[] featuressorted = features.clone();
		int[] indexes = new int[n];
		Arrays.sort(featuressorted);
		for (int i = 0; i < n; i++) {
			indexes[i] = getArrayIndex(features, featuressorted[i]);
		}
		return indexes;
	}
	private String[] extractGenres(double[] features, int num) {
		int[] indexes = getLowestIndexes(num, features);
		String[] genres = new String[num];
		for (int i = 0; i < indexes.length; i++) {
			genres[i] = DictionaryAndFeatureGenerator.GENRES[indexes[i]];
		}
		
		return genres;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public String[] getGenres() {
		return genres;
	}
}
