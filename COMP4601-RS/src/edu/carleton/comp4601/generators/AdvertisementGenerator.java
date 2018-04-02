package edu.carleton.comp4601.generators;

import java.util.ArrayList;
import java.util.HashMap;

import Jama.Matrix;
import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.data.util.Kmeans;
import edu.carleton.comp4601.model.Advertisement;
import edu.carleton.comp4601.model.Advertisements;
import edu.carleton.comp4601.users.UserProfile;

public class AdvertisementGenerator {
	
	
	public static void generateAdvertisements(HashMap<String, ArrayList<UserProfile>> clusters) {

		for (String key : clusters.keySet()) {
			ArrayList<UserProfile> users = clusters.get(key);
			Matrix accumMatrix = new Matrix(1,DictionaryAndFeatureGenerator.GENRES.length);
			for (UserProfile user : users) {
				accumMatrix.plusEquals(user.getNewFeaturesAsMatrix());
			}
			for (int i = 0; i < accumMatrix.getColumnDimension(); i++) {
				accumMatrix.getArray()[0][i] = accumMatrix.getArray()[0][i] / users.size();
			}
			
			Advertisement advertisement = new Advertisement(accumMatrix.getArray()[0], key);
			Advertisements.getInstance().addAdvertisement(advertisement);
		}
		
	}
}
