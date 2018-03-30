package edu.carleton.comp4601.RS.resources;

import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.ArrayUtils;

import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.dao.Document;
import edu.carleton.comp4601.data.util.Kmeans;
import edu.carleton.comp4601.generators.AdvertisementGenerator;
import edu.carleton.comp4601.users.UserProfile;


@Path("/rs")
public class RS {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private String name;

	public RS() {
		ArrayList<UserProfile> profiles = DatabaseManager.getInstance().loadUserProfiles();
		//Kmeans kmeans = new Kmeans(7, profiles);
		//HashMap<String, ArrayList<UserProfile>> clusters = kmeans.algorithm();
		HashMap<String, ArrayList<UserProfile>> clusters = DatabaseManager.getInstance().loadClustersFullUser();
		AdvertisementGenerator.generateAdvertisements(clusters);
		name = "COMP4601 Recomender System V2.1: Julian and Laura";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String rs() {
		return "<html><head><title>COMP 4601</title></head><body><h1>"+ name +"</h1></body></html>";
	}
	
	@GET
	@Path("context")
	@Produces(MediaType.TEXT_HTML) 
	public String context() {
		DatabaseManager dbm = DatabaseManager.getInstance();

		ArrayList<UserProfile> users = dbm.loadUserProfiles();
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Users </title></head>");
		htmlBuilder.append("<h1> Total users: " + users.size() + "</h1>");
		htmlBuilder.append("<body>");
		htmlBuilder.append("<table style=\"width:100%\">");	
		for(UserProfile user : users) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>" + user.getUsername() + "</td>");
			htmlBuilder.append("<td>" + user.getNewFeaturesString() + "</td>");
			htmlBuilder.append("</tr>");
		}		
		return htmlBuilder.toString();
	}
	@GET
	@Path("community")
	@Produces(MediaType.TEXT_HTML) 
	public String community() {
		DatabaseManager dbm = DatabaseManager.getInstance();

		HashMap<String, ArrayList<String>> clusters = dbm.loadClusters();
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Communities </title></head>");
		htmlBuilder.append("<h1> Communities </h1>");
		htmlBuilder.append("<body>");
		htmlBuilder.append("<table style=\"width:100%\">");
		for (String key : clusters.keySet()) {
			htmlBuilder.append("<tr>");
			htmlBuilder.append("<td>" + formatClusterTitle(key) + "</td>");
			htmlBuilder.append("</tr>");
			for(String username : clusters.get(key)) {
				htmlBuilder.append("<td>" + username + "</td>");
				htmlBuilder.append("</tr>");
			}
		}
		return htmlBuilder.toString();
	}
	
	private String formatClusterTitle(String cluster) {
		int num  = Integer.parseInt(cluster.substring(cluster.length() - 1));
		return "C-" + num;
	}
	
}
