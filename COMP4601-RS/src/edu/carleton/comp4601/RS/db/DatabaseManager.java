package edu.carleton.comp4601.RS.db;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.model.Dictionary;
import edu.carleton.comp4601.model.GenreReviews;
import edu.carleton.comp4601.model.GenreReviews.GenreReview;
import edu.carleton.comp4601.model.Movie;
import edu.carleton.comp4601.users.UserProfile;


public class DatabaseManager {
	
	private final String MOV_COL = "movies";
	private final String DOC_NUM_COL = "docnum";
	private final String REVIEW_COL = "reviews";
	private final String DICT_COL = "dictionaries";
	private final String USERPROFILE_COL = "userprofiles";
	private final String CLUSTERS_COL = "clusters";
	
	private MongoClient	m;
	private DBCollection col;
	private DB db;
	private static DatabaseManager instance;
	
	public DatabaseManager() {
		instance = this;
		initConnection();
	}

	private int getDocNum() {
		switchCollection(DOC_NUM_COL);
		DBCursor cur = col.find().limit(1);
		int num = 1000;
		if (cur.hasNext()) {
			DBObject obj = cur.next();
			num = (int) obj.get("docnum");
		}
		return num;
	}
	
	public void incrementDocNum() {
		switchCollection(DOC_NUM_COL);
		DBCursor cur = col.find().limit(1);
		int num = 0;
		DBObject obj;
		if (cur.hasNext()) {
			obj = cur.next();
			num = (int) obj.get("docnum");
		}
		col.remove(new BasicDBObject("name","docid"));
		num++;
		DBObject newDocId = BasicDBObjectBuilder.start("name", "docid").add("docnum", num).get();
		col.insert(newDocId);
	}
		
	public void addMovieToDb(String id, String title, String review, List<String> genres) {
		incrementDocNum();
		switchCollection(MOV_COL);
		DBObject obj = BasicDBObjectBuilder
				.start("id", id)
				.add("name", title)
				.add("genres", genres)
				.add("reviews", review)
				.get();

		col.save(obj);
	}
	public static DatabaseManager getInstance() {
		if (instance == null)
			instance = new DatabaseManager();
		return instance;
	}
	
	public static void setInstance(DatabaseManager instance) {
		DatabaseManager.instance = instance;	
	}
	
	private void initConnection() {
		try {
			m = new	MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}	
		db = m.getDB("rs");	
		switchCollection(MOV_COL);
	}
	
	private void switchCollection(String collection) {
		col = db.getCollection(collection);
	}
	
	public ArrayList<Movie> loadMovies() {
		switchCollection(MOV_COL);
		DBCursor cursor = col.find();
		ArrayList<Movie> movies = new ArrayList<Movie>();
		DBObject obj = null;
		while(cursor.hasNext()) {
			obj = cursor.next();
			Movie movie = new Movie(
					(String)obj.get("id"),
					(String)obj.get("name"),
					(List<String>) obj.get("genres"),
					(String)obj.get("reviews")
					);
			movies.add(movie);
		}
		
		return movies;
	}

	public boolean dropMovies() {
		switchCollection(MOV_COL);
		BasicDBObject document = new BasicDBObject();
		col.remove(document);
		DBCursor cursor = col.find();
		boolean success = false;
		while (cursor.hasNext()) {
		    col.remove(cursor.next());
		    success = true;
		}
		return success;
	}
	
	public void writeReviewsToDb(HashMap<String, String> reviews) {
		switchCollection(REVIEW_COL);
		for (String genre : reviews.keySet()) {
			DBObject obj = BasicDBObjectBuilder
					.start("genre", genre)
					.add("reviews", reviews.get(genre))
					.get();
			col.save(obj);
		}
	}
	public void loadReviews() {
		switchCollection(REVIEW_COL);
		GenreReviews gr = GenreReviews.getInstance();
		DBCursor cursor = col.find();
		ArrayList<GenreReview> movies = new ArrayList<GenreReview>();
		DBObject obj = null;
		while (cursor.hasNext()) {
			obj = cursor.next();
			gr.addReview(
					(String) obj.get("genre"), 
					(String) obj.get("reviews"));
		}
	}
	public void addDictionaryToDb(ArrayList<String> words, GenreReview r) {
		switchCollection(DICT_COL);
		DBObject obj = BasicDBObjectBuilder
				.start("genre", r.getGenre())
				.add("dict", words)
				.get();

		col.save(obj);
	}
	public ArrayList<Dictionary> loadDictionariesFromDb() {
		switchCollection(DICT_COL);
		DBCursor cursor = col.find();
		ArrayList<Dictionary> dictionaries = new ArrayList<Dictionary>();
		DBObject obj = null;

		while (cursor.hasNext()) {
			obj = cursor.next();
			dictionaries.add(new Dictionary(
					(String) obj.get("genre"), 
					(ArrayList<String>) obj.get("dict")));
		}
		return dictionaries;
	}
	public void saveUserProfileToDatabase(UserProfile user) {
		switchCollection(USERPROFILE_COL);
		DBObject obj = BasicDBObjectBuilder
				.start("username", user.getUsername())
				.add("features", user.getFeatures())
				.get();

		col.save(obj);
	}
	public ArrayList<UserProfile> loadUserProfiles() {
		switchCollection(USERPROFILE_COL);
		DBCursor cursor = col.find();
		DBObject obj = null;
		ArrayList<UserProfile> userProfiles = new ArrayList<UserProfile>();
		while (cursor.hasNext()) {
			obj = cursor.next();
			ArrayList<Double> list = (ArrayList<Double>) obj.get("features");
			userProfiles.add(new UserProfile(
					(String) obj.get("username"),
					list));
		}
		return userProfiles;
	}
	private ArrayList<String> getUsernames(ArrayList<UserProfile> userprofiles) {
		ArrayList<String> usernames = new ArrayList<String>();
		for (UserProfile userprofile : userprofiles) {
			usernames.add(userprofile.getUsername());
		}
		return usernames;
	}
	public void saveClustersToDb(HashMap<String, ArrayList<UserProfile>> clusters) {
		switchCollection(CLUSTERS_COL);
		
		for (String key : clusters.keySet()) {
			DBObject obj = BasicDBObjectBuilder
					.start("cluster", key)
					.add("users", getUsernames(clusters.get(key)))
					.get();
	
			col.save(obj);
		}
	}
}
