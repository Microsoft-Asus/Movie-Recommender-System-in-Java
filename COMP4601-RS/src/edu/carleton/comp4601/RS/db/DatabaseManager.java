package edu.carleton.comp4601.RS.db;
import java.io.IOException;
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
import com.mongodb.MongoException;


public class DatabaseManager {
	
	private final String USER_COL = "users";
	private final String MOV_COL = "movies";
	private final String DOC_NUM_COL = "docnum";
	
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

	
}
