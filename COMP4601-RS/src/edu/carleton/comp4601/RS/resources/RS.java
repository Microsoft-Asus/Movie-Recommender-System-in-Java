package edu.carleton.comp4601.RS.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


@Path("/rs")
public class RS {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	private String name;

	public RS() {
		name = "COMP4601 Recomender System V2.1: Julian and Laura";
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String rs() {
		return "<html><head><title>COMP 4601</title></head><body><h1>"+ name +"</h1></body></html>";
	}
	
	
}
