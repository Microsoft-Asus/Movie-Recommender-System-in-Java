package edu.carleton.comp4601.model;

import java.util.ArrayList;

public class Dictionary {

	String genre;
	ArrayList<String> dictionary;
	
	public Dictionary(String genre, ArrayList<String> dictionary) {
		this.genre = genre;
		this.dictionary = dictionary;
	}
	public String getGenre() {
		return genre;
	}
	public ArrayList<String> getDictionary() {
		return dictionary;
	}
}
