package edu.carleton.comp4601.model;

import java.util.ArrayList;

import edu.carleton.comp4601.RS.db.DatabaseManager;

public class GenreReviews {
	
	ArrayList<GenreReview> reviews;
	private static GenreReviews instance;
	
	public GenreReviews() {
		this.reviews = new ArrayList<GenreReview>();
		instance = this;
	}
	public static GenreReviews getInstance() {
		if (instance == null) {
			instance = new GenreReviews();
		}
		return instance;
	}
	public void setReviews(ArrayList<GenreReview> reviews) {
		this.reviews = reviews;
	}
	public ArrayList<GenreReview> getReviews() {
		return reviews;
	}
	public void addReview(String genre, String review) {
		reviews.add(new GenreReview(genre, review));
	}
	public class GenreReview {
		String genre;
		String reviews;
		
		public GenreReview(String genre, String reviews) {
			this.genre = genre;
			this.reviews = reviews;
		}
		public String getGenre() {
			return genre;
		}
		public String getReviews() {
			return reviews;
		}
	}
}
