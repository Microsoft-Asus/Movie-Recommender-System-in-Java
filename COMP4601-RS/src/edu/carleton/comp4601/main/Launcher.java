package edu.carleton.comp4601.main;

import java.util.ArrayList;
import java.util.HashMap;

import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.data.util.Kmeans;
import edu.carleton.comp4601.generators.AdvertisementGenerator;
import edu.carleton.comp4601.model.Advertisement;
import edu.carleton.comp4601.model.Advertisements;
import edu.carleton.comp4601.users.UserProfile;

public class Launcher {
	
	public static void main(String[] args) {
		ArrayList<UserProfile> profiles = DatabaseManager.getInstance().loadUserProfiles();
		Kmeans kmeans = new Kmeans(7, profiles);
		HashMap<String, ArrayList<UserProfile>> clusters = kmeans.algorithm();
		
		AdvertisementGenerator.generateAdvertisements(clusters);
		Advertisements advertisements = Advertisements.getInstance();
		ArrayList<UserProfile> users = DatabaseManager.getInstance().loadUserProfiles();
		System.out.println("All ads");
		for (Advertisement advertisement : advertisements.getAdvertisements()) {
			System.out.println(advertisement.getHtml());
		}
		
		System.out.println("User test");
		UserProfile testUser = users.get(547);
		ArrayList<Advertisement> releventAds = testUser.getClosestAdvertisements(2);
		System.out.println(testUser.getUsername());
		System.out.println(testUser.getFavouriteGenre());
		for (double f : testUser.getNewFeatures()) {
			System.out.println(f);
		}
		
		for (Advertisement ad : releventAds) {
			System.out.println(ad.getHtml());
		}


		
		
	}

}
