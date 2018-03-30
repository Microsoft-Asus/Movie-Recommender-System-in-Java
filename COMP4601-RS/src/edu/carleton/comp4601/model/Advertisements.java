package edu.carleton.comp4601.model;

import java.util.ArrayList;

public class Advertisements {
	
	private ArrayList<Advertisement> advertisements;
	private static Advertisements instance;
	
	public Advertisements() {
		advertisements = new ArrayList<Advertisement>();
		instance = this;
	}
	public static Advertisements getInstance() {
		if (instance == null) {
			instance = new Advertisements();
		}
		return instance;
	}
	
	public void addAdvertisement(Advertisement advertisement) {
		advertisements.add(advertisement);
	}
	public ArrayList<Advertisement> getAdvertisements() {
		return advertisements;
	}

}
