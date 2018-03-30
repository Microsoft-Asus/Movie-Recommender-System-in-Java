package edu.carleton.comp4601.RS.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import edu.carleton.comp4601.RS.db.DatabaseManager;
import edu.carleton.comp4601.generators.AdvertisementGenerator;
import edu.carleton.comp4601.lucene.Lucene;
import edu.carleton.comp4601.model.Advertisement;
import edu.carleton.comp4601.model.Advertisements;
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
		if (Advertisements.getInstance().getAdvertisements().isEmpty()){
			AdvertisementGenerator.generateAdvertisements(clusters);
		}
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
	@Path("reset/{DIR}")
	@Produces(MediaType.TEXT_HTML) 
	//public String reset(@PathParam("DIR") String directory) {
	public String reset(@PathParam("DIR") String directory) {
		DatabaseManager dbm = DatabaseManager.getInstance();
		
		Lucene luc = Lucene.getInstance(directory);
		luc.indexLucene();
		
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Reset </title></head>");
		htmlBuilder.append("<h1> Initializing data at " + directory + " </h1>");
		
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
	
	@GET
	@Path("advertising/{CATEGORY}")
	@Produces(MediaType.TEXT_HTML)
	public String advertising(@PathParam("CATEGORY") String id) {
		Advertisement advertisement = Advertisements.getInstance().getAdvertisementById(id);
		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append("<html>");
		htmlBuilder.append("<head><title> Advertisement </title></head>");
		htmlBuilder.append("<h1> For those who like:  </h1>");
		htmlBuilder.append("<body>");
		htmlBuilder.append("<table style=\"width:100%\">");
		htmlBuilder.append(advertisement.getGenres()[0]);
		for (int i = 1; i < advertisement.getGenres().length; i++) {
			htmlBuilder.append(", " + advertisement.getGenres()[i]);
		}
		return htmlBuilder.toString();
	}
	public String getPageHtml(String theurl)  {
		URL url;
		StringBuilder buffer = new StringBuilder();
		try {
			url = new URL(theurl);
			InputStream is = url.openStream();
			int ptr = 0;
			while ((ptr = is.read()) != -1) {
			    buffer.append((char)ptr);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();

	}
	@GET
	@Path("fetch/{USER}/{PAGE}")
	@Produces(MediaType.TEXT_HTML)
	public String fetchPageWithAdveritising(@PathParam("USER") String userid, @PathParam("PAGE") String pageid) {
		UserProfile user = DatabaseManager.getInstance().loadUserByName(userid);
		ArrayList<Advertisement> ads = user.getClosestAdvertisements(1);
		StringBuilder listhtml = new StringBuilder();
		String html = Lucene.getInstance().queryPage(pageid);
		System.out.println(html);
		for (Advertisement ad : ads) {
			listhtml.append("<li>");
			listhtml.append(ad.getGenres()[0]);
			for (int i = 1; i < ad.getGenres().length;i++) {
				listhtml.append(", " + ad.getGenres()[i]);
			}
			listhtml.append("</li>");
		}
		return "<!DOCTYPE html><html><head><style>div.container {width: 100%;border: 1px solid gray;}header, footer {padding: 1em;color: white;background-color: black;clear: left;text-align: center;}nav {float: left;max-width: 160px;margin: 0;padding: 1em;}nav ul {list-style-type: none;padding: 0;}nav ul a {text-decoration: none;}article {margin-left: 170px;border-left: 1px solid gray;padding: 1em;overflow: hidden;}</style></head><body><div class=\"container\"><header><h1>Movie "+ pageid + "</h1></header><nav><ul>" + listhtml.toString() + "</ul></nav><article><h1>" + pageid + "</h1> <p>"+ html + "</p></article><footer></footer></div></body></html>";
	}
	
}
