package de.l3s.eumssi.webservices;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import de.l3s.eumssi.dao.SolrDBManager_tan;
import de.l3s.eumssi.model.Articles_tan;

@Path("/API_tan/")
public class RestfulService_tan {
	
	@GET
	@Path("/getArticles/json/{parameter_headline}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Articles_tan> getArticles(@PathParam("parameter_headline") String parameter_headline) throws Exception{
		ArrayList<Articles_tan> articles;
		SolrDBManager_tan solrDBManager = new SolrDBManager_tan();
		articles = solrDBManager.getArticles(parameter_headline);
		return articles;
	}
	
}