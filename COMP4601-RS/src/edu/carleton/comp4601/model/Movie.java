package edu.carleton.comp4601.model;

import java.util.ArrayList;
import java.util.List;

public class Movie {

	String id;
	String title;
	List<String> genres;
	String reviews;
	
	public Movie(String id, String title, List<String> genres, String reviews) {
		this.genres = genres;
		this.id = id;
		this.title = title;
		this.genres = genres;
		this.reviews = reviews;
	}
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public List<String> getGenres() {
		return genres;
	}

	public String getReviews() {
		return reviews;
	}
}
