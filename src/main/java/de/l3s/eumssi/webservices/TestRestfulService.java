package de.l3s.eumssi.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import de.l3s.eumssi.model.Entity;
import de.l3s.eumssi.model.Event;

@Path("/API/")
public class TestRestfulService {

	private static final String SOLR_SERVER_URL = "http://eumssi.cloudapp.net/Solr_EUMSSI/content_items/";

	private static final String FIELD_SOURCE = "source";
	private static final String FIELD_ORGANIZATION = "meta.extracted.text.ner.ORGANIZATION";
	private static final String FIELD_HEADLINE = "meta.source.headline";
	private static final String FIELD_NER_ALL = "meta.extracted.text.ner.all";

	private static final String FIELD_VALUE_DW_VIDEO = "DW video";

	private static final String[] RETURN_FILEDS = { FIELD_HEADLINE,
			FIELD_NER_ALL };

	private HttpSolrServer server;

	public TestRestfulService() {
		server = new HttpSolrServer(SOLR_SERVER_URL);
	}

	@GET
	@Path("/getDWVideoByOrganization/json/{organization}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getDWVideoByOrganization(
			@PathParam("organization") String query) {
		List<Event> events = new ArrayList<>();

		if (query == null || query.isEmpty())
			return events;

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(FIELD_ORGANIZATION + ":" + query);
		solrQuery.setFilterQueries(FIELD_SOURCE + ":" + FIELD_VALUE_DW_VIDEO);
		for (String field : RETURN_FILEDS)
			solrQuery.addField(field);

		try {
			QueryResponse response = server.query(solrQuery);
			SolrDocumentList results = response.getResults();

			for (SolrDocument doc : results) {
				Event e = solrDocToEvent(doc);
				if (e != null)
					events.add(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return events;
	}

	private Event solrDocToEvent(SolrDocument doc) {
		// get headline
		String headline = null;
		Object headlineObj = doc.getFieldValue(FIELD_HEADLINE);
		if (headlineObj != null)
			headline = headlineObj.toString();

		// get ner.all
		ArrayList<Entity> entities = null;
		Collection<Object> entityObjs = doc.getFieldValues(FIELD_NER_ALL);
		if (entityObjs != null) {
			entities = new ArrayList<>();
			Set<String> nameSet = new HashSet<>();
			for (Object obj : entityObjs) {
				String name = obj.toString();
				if (nameSet.contains(name))
					continue;
				nameSet.add(name);
				Entity e = new Entity();
				e.setName(name);
				entities.add(e);
			}
		}

		Event e = new Event();
		e.setHeadline(headline);
		e.setEntities(entities);

		return e;
	}

}
