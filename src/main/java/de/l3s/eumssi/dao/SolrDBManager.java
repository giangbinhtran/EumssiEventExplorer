/**
 * db manager for mongodb 
 * 
 * gtran@l3s.de
 * Nov 28 2014
 */
package de.l3s.eumssi.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.l3s.eumssi.core.EventDistribution;
import de.l3s.eumssi.core.Stopwords;
import de.l3s.eumssi.core.StoryDistribution;
import de.l3s.eumssi.core.sortingMap;
import de.l3s.eumssi.model.Entity;
import de.l3s.eumssi.model.Event;
import de.l3s.eumssi.model.Reference;
import de.l3s.lemma.lemma;


class Gram {
	String u, v;
	int frq;
	public Gram (String word1, String word2, int count) {
		u = word1;
		v = word2;
		frq = count;
	}
	
	public String getWord1() { return u;}
	public String getWord2(){ return v;}
}
public class SolrDBManager {
	HttpSolrServer solr;
	public Properties conf;
	public SolrDBManager() {
		/*
		lemma.init();
		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		solr = new HttpSolrServer("http://eumssi.cloudapp.net/Solr_EUMSSI/content_items/");
	}
	
	
	public void loadConfiguration() throws FileNotFoundException, IOException{
		conf = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		conf.load(classLoader.getResourceAsStream("DBHandler.properties"));		
	}
	
	
	 public JSONObject toJSONObject(Event event, Object contextPath) {
			JSONObject eventObject = null;
			try {
				eventObject = new JSONObject();
				eventObject.put("startDate", new SimpleDateFormat("yyyy,MM,dd").format(event.getDate()));
				eventObject.put("endDate", new SimpleDateFormat("yyyy,MM,dd").format(event.getDate()));
				eventObject.put("headline", event.getHeadline());
				String text = "";
				if (event.getStory()!=null){
					 if (ServletActionContext.getRequest().getServerName().equals("wikitimes.l3s.de")){        			
			    			text = "<a href=\"/storyTimeline.action?storyId=" +event.getStory().getId() + "\">" +
			    					  		"<font color=\"#0040FF\" >" + event.getStory().getName()+ "</font>" +
			    					  	"</a>";
			    		}else{        			
			    			text = "<a href=\""+contextPath+"/storyTimeline.action?storyId=" +event.getStory().getId()+"\">" +
			    							"<font color=\"#0040FF\" >" + event.getStory().getName()+ "</font>" +
			    						"</a>" ;
			    		}
//					text = event.getStory().getName();
					//storyTimeline.action?storyId=177
				}
				eventObject.put("text", text);
//				if(event.getReferences()!=null){
//					if(!event.getReferences().isEmpty()){
//						JSONObject asset = new JSONObject();
//						asset.put("media", event.getReferences().get(0).getUrl());
//						asset.put("caption", event.getReferences().get(0).getSource());
//						eventO.put("asset", asset);
//					}
//				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return eventObject;
		}
	 
	public JSONObject getTimelineJSON(List<Event> events, Object contextPath){
    	
    	JSONObject timeline = null;
    	
    	List<JSONObject> eventsJSObjects = new ArrayList<JSONObject>();
    	for (Event event: events){
    		eventsJSObjects.add(toJSONObject(event, contextPath));
    	}
    	
		try {
			JSONObject content = new JSONObject();
			content.put("headline:", "The Main Timeline Headline");
			content.put("type", "default");
			content.put("text", "<p>Intro body text goes here, some HTML is ok</p>\\");
			content.put("date", new JSONArray(eventsJSObjects));
			timeline = new JSONObject();
			timeline.put("timeline", content);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		return timeline;
    }
	
	public void test() throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery("source:\"Youtube-video-GeneralChannel\"");
		 
		QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    System.out.println(results.size());
	    for (int i = 0; i < 10; ++i) {
	      System.out.println(results.get(i).getFieldValue("meta.source.rtspHigh"));
	    }
	}
	
	public ArrayList<String> getField(String source, String field) {
		ArrayList<String> array_results = new ArrayList<String> ();
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		query.addFilterQuery("source:" + source);
		query.setFields(field);
		query.setRows(20);
		System.out.println(query.toString());
		QueryResponse response;
		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    System.out.println(results.size());
		    for (int i = 0; i < results.size(); ++i) {
		      array_results.add(results.get(i).getFieldValue(field).toString());
		    }
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	    
	    return array_results;
	}
	
	public String formulateQueryMultipleFields(ArrayList<String> searchfields, String keyword) {
		ArrayList<String> qstr = new ArrayList<String> ();
		for (String s: searchfields) qstr.add(String.format("%s:\"*%s*\"\t", s, keyword));
		String[] tmp = (String[]) qstr.toArray(new String[qstr.size()]);
		String q = StringUtils.join(tmp, " OR ");
		return q;
	}
	
	public StoryDistribution getDistribution(String keyword, ArrayList<String> sources, ArrayList<String> searchfields, int maxNumOfEvents) {
		SolrQuery query = new SolrQuery();
		String source = formulateQuerySimple(sources);
		
		if (keyword==null || keyword.equals("")) {
			for (String searchField: searchfields) {
				query.setQuery(String.format("%s:*\t", searchField));
			}
			System.out.println("debug: null/empty query");
		}
		else {
			String queryString = formulateQueryMultipleFields(searchfields, keyword);
			query.setQuery(queryString);
		}
		query.addFilterQuery("source:" + source);
		query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.setFields( "meta.source.datePublished");
		for (String searchField: searchfields) {query.addField(searchField);}
		
		query.setRows(maxNumOfEvents);
		System.out.println(query.toString());
		QueryResponse response;
		StoryDistribution sd = new StoryDistribution();
		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    System.out.println(results.size());
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	for (String searchField: searchfields) {
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		sb.append(fieldVal.toString());
			    		sb.append("\n");
		    		}
		    	}
		    	String fieldText = sb.toString();
		    	
		    	int len = Math.min(fieldText.length(), 1000);
		    	fieldText = fieldText.substring(0, len-1);
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	if (fieldText!=null) {
		    		EventDistribution e = new EventDistribution(fieldText, date);
		    		sd.index(e);
		    	}
		    }
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	    return sd;
	}
	
	public StoryDistribution getDistribution(String solrquery) {
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", 
				"meta.source.publisher", "meta.source.text");
		query.setQuery(solrquery);
		
		
		query.setRows(300);
		StoryDistribution sd = new StoryDistribution();
		System.out.println("SearchByKeyword" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	String headline = null;
		    	String url = null;
		    	for (String searchField: new String[] {"meta.source.text", "meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.publisher"}) 
		    	{
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		
		    			if (searchField.equals("meta.source.text")) {
			    			sb.append(results.get(i).getFieldValue("meta.source.text").toString());
			    		}
		    			
			    		if (searchField.equals("meta.source.headline")) {
			    			headline = results.get(i).getFieldValue("meta.source.headline").toString();
			    			//headline = clean(headline);
			    		}
			    		
			    		if (searchField.equals("meta.source.url")) {
			    			Object uObj = results.get(i).getFieldValue("meta.source.url");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
			    			if (uObj!= null) {
			    				url  = uObj.toString(); 
			    			}
			    		}
		    		}
		    	}
		    	String fieldText = sb.toString(); // short description
		    	
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}
		    	
		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = null;
		    	if (date!= null )
		    		sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text.dbpedia.all");
		    	if (entityObject!=null)  {
			    	for (Object oe: entityObject) {
			    		String entityName = oe.toString();
			    		Entity e = new Entity();
			    		e.setName(entityName);
			    		dbentities.add(e);
			    	}
		    	}
		    	
		    	
		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate()!=null && e.getDate().toString().compareTo("2050")<0) { 
		    		//ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			selectedTitles.add(headline);
		    			EventDistribution evt = new EventDistribution(e.getDescription(), date);
			    		sd.index(evt);
		    		}
		    	}
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	    return sd;
	}
	
	
	
	private String formulateQuerySimple(ArrayList<String> sources) {
		ArrayList<String> qfilter = new ArrayList<String> ();
		for (String s: sources) {
			if (s.equals("NewsArticle")) {
				qfilter.add("\"News\"");
				qfilter.add("\"DW article\"");
			}
			if (s.equals("Video")) {
				qfilter.add("\"DW video\"");
				qfilter.add("\"Youtube\"");
				qfilter.add("\"DW (Youtube)\"");
				qfilter.add("\"Guardian (Youtube)\"");
			}
		}
		String[] tmp = (String[]) qfilter.toArray(new String[qfilter.size()]);
		String strqfilter = StringUtils.join(tmp, " OR ");
		System.out.println("Sources for filter: " + strqfilter);
		return "(" + strqfilter + ")";
	}
	/*
	 * get most recent n items 
	 */
	public List<Event> searchByKeyword(String keyword, ArrayList<String> sources, ArrayList<String> searchfields, int n_items) {
		HashSet<String> selectedTitles = new HashSet<String> ();
		ArrayList<Event> itemList = new ArrayList<Event> ();
		SolrQuery query = new SolrQuery();
		String source = formulateQuerySimple(sources);
		
		if (keyword==null || keyword.equals("")) {
			for (String searchField: searchfields) {
				query.setQuery(String.format("%s:*\t", searchField));
			}
			System.out.println("debug: null/empty query");
		}
		else {
			String queryString = formulateQueryMultipleFields(searchfields, keyword);
			query.setQuery(queryString);
		}
		query.addFilterQuery("source:" + source);
		//query.addFilterQuery("meta.source.inLanguage:\"en\"");
		//--------------------------------------------------------------------
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.httpHigh", 
				"meta.source.publisher");
		for (String searchField: searchfields) {query.addField(searchField);}
		
		
		//query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.setSort("meta.source.datePublished", ORDER.desc);
		query.setRows(n_items);
		System.out.println("SearchByKeyword " + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	for (String searchField: searchfields) {
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		sb.append(fieldVal.toString());
			    		sb.append("\n");
		    		}
		    	}
		    	String fieldText = sb.toString();
		    	String headline = results.get(i).getFieldValue("meta.source.headline").toString();
		    	headline = clean(headline);
		    	String url = null;
		    	Object uObj = results.get(i).getFieldValue("meta.source.url");
		    	if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.httpHigh");
		    	
		    	if (uObj!= null) {
		    		url  = uObj.toString(); 
		    	}
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}
		    	
		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	
		    	Event e = new Event();
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate().toString().compareTo("2050")<0) { //ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (SolrServerException ex) {
			ex.printStackTrace();
		}
	    return itemList;
	}
	
	
	
	/*
	 * get most recent n items 
	 */
	public List<Event> videoSearch(String keyword, ArrayList<String> sources, ArrayList<String> searchfields, int n_items) {
		HashSet<String> selectedTitles = new HashSet<String> ();
		ArrayList<Event> itemList = new ArrayList<Event> ();
		SolrQuery query = new SolrQuery();
		String source = formulateQuerySimple(sources);
		
		if (keyword==null || keyword.equals("")) {
			for (String searchField: searchfields) {
				query.setQuery(String.format("%s:*\t", searchField));
			}
			System.out.println("debug: null/empty query");
		}
		else {
			String queryString = formulateQueryMultipleFields(searchfields, keyword);
			query.setQuery(queryString);
		}
		query.addFilterQuery("source:" + source);
		query.addFilterQuery("meta.source.inLanguage:\"en\"");
		//--------------------------------------------------------------------
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.mediaurl", 
				"meta.source.publisher", "meta.extracted.text.dbpedia.all");
		for (String searchField: searchfields) {query.addField(searchField);}
		
		
		query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.setSort("meta.source.datePublished", ORDER.desc);
		query.setRows(n_items);
		System.out.println("SearchByKeyword " + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	for (String searchField: searchfields) {
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		sb.append(fieldVal.toString());
			    		sb.append("\n");
		    		}
		    	}
		    	String fieldText = sb.toString();
		    	String headline = results.get(i).getFieldValue("meta.source.headline").toString();
		    	headline = clean(headline);
		    	String url = null;
		    	Object uObj = results.get(i).getFieldValue("meta.source.url");
		    	if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
		    	
		    	if (uObj!= null) {
		    		url  = uObj.toString(); 
		    	}
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}
		    	
		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text.dbpedia.all");
		    	if (entityObject!=null)
		    	for (Object oe: entityObject) {
		    		String entityName = oe.toString();
		    		Entity e = new Entity();
		    		e.setName(entityName);
		    		dbentities.add(e);
		    	}
		    	
		    	
		    	
		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate().toString().compareTo("2050")<0) { //ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline) && e.getEntities().size()>0) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (SolrServerException ex) {
			ex.printStackTrace();
		}
	    return itemList;
	}
	
	
	
	public String clean(String headline) {
		int p = headline.indexOf("|");
		if (p>0) {
			return headline.substring(0, p);
		}
		return headline;
	}


	/**
	 * get all items on the given date from db
	 * @param storyDate in from of yyyy-mm-dd
	 * @return
	 */
	public ArrayList<Event> getItemsByDate(String storyDate, String source, String searchfield) {
		int n_items = 100; // max-output of a date
		ArrayList<Event> itemList = new ArrayList<Event> ();
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		query.addFilterQuery(String.format("meta.source.datePublished:[%sT0:00:00Z TO %sT23:59:59Z]", storyDate, storyDate));
		query.addFilterQuery("source:" + source);
		query.setFields(searchfield, "meta.source.datePublished", "meta.source.headline", "meta.source.url", 
				"meta.source.publisher");
		query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.setSort("meta.source.datePublished", ORDER.desc);
		query.setRows(n_items);
		System.out.println("SearchByKeyword " + query.toString());
		QueryResponse response;
		StoryDistribution sd = new StoryDistribution();
		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	String fieldText = results.get(i).getFieldValue(searchfield).toString();
		    	String headline = results.get(i).getFieldValue("meta.source.headline").toString();
		    	headline = clean(headline);
		    	String url = results.get(i).getFieldValue("meta.source.url").toString();
		    	String publisher = results.get(i).getFieldValue("meta.source.publisher").toString();
		    	
		    	Reference ref = new Reference(url, url, publisher);
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	
		    	Event e = new Event();
		    	e.setDescription(fieldText.trim());
		    	e.setDate(sqldate);
		    	e.setHeadline(headline.trim());
		    	
		    	//no need for the references
		    	///e.addReference(ref);
		    	
		    	itemList.add(e);
		    }
		} catch (SolrServerException ex) {
			ex.printStackTrace();
		}
	    return itemList;
	}

	
	// using solrformated query to get the list of events from the solr db 
	// and the filtering to keep n numebr of events
	public List<Event> getImportantEvents(int n, String solrFormatedQuery) {
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", 
				"meta.source.publisher", "meta.source.text", "meta.extracted.text.ner.all");
		query.setQuery(solrFormatedQuery);
		
		
		query.setRows(5*n);		
		System.out.println("SearchByKeyword" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	String headline = null;
		    	String url = null;
		    	for (String searchField: new String[] {"meta.source.text", "meta.source.datePublished", 
		    			"meta.source.headline", "meta.source.url", "meta.source.publisher", 
		    			"meta.extracted.text.ner.all"}) 
		    	{
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		
		    			if (searchField.equals("meta.source.text")) {
			    			sb.append(results.get(i).getFieldValue("meta.source.text").toString());
			    		}
		    			
			    		if (searchField.equals("meta.source.headline")) {
			    			headline = results.get(i).getFieldValue("meta.source.headline").toString();
			    			headline = clean(headline);
			    		}
			    		
			    		if (searchField.equals("meta.source.url")) {
			    			Object uObj = results.get(i).getFieldValue("meta.source.url");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
			    			if (uObj!= null) {
			    				url  = uObj.toString(); 
			    			}
			    		}
		    		}
		    	}
		    	String fieldText = sb.toString().substring(0, Math.min(300, sb.length())) + "..."; // short description
		    	
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}
		    	
		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = null;
		    	if (date!= null )
		    		sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	HashMap<String, Integer> en_hash = new HashMap<String, Integer> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text.ner.all");
		    	if (entityObject!=null)  {
			    	for (Object oe: entityObject) {
			    		String entityName = oe.toString();
			    		Entity e = new Entity();
			    		
			    		e.setName(entityName);
			    		if (!en_hash.containsKey(entityName) && dbentities.size()<10)
			    			dbentities.add(e);
			    		en_hash.put(entityName, 1);
			    	}
		    	}
		    	
		    	
		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate()!=null && e.getDate().toString().compareTo("2050")<0) { 
		    		//ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		System.out.println("successfully returns " + itemList.size());
	    
		return itemList;
	}
	
	
	public List<Event> searchBySolrQuery(int n, String solrFormatedQuery) {
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", 
				"meta.source.publisher", "meta.source.text");
		query.setQuery(solrFormatedQuery);
		
		
		query.setRows(5*n);		
		System.out.println("SearchByKeyword" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	String headline = null;
		    	String url = null;
		    	for (String searchField: new String[] {"meta.source.text", "meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.publisher"}) 
		    	{
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		
		    			if (searchField.equals("meta.source.text")) {
			    			sb.append(results.get(i).getFieldValue("meta.source.text").toString());
			    		}
		    			
			    		if (searchField.equals("meta.source.headline")) {
			    			headline = results.get(i).getFieldValue("meta.source.headline").toString();
			    			headline = clean(headline);
			    		}
			    		
			    		if (searchField.equals("meta.source.url")) {
			    			Object uObj = results.get(i).getFieldValue("meta.source.url");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
			    			if (uObj!= null) {
			    				url  = uObj.toString(); 
			    			}
			    		}
		    		}
		    	}
		    	String fieldText = sb.toString().substring(0, Math.min(300, sb.length())) + "..."; // short description
		    	
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}
		    	
		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}
		    	
		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = null;
		    	if (date!= null )
		    		sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	
		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text.dbpedia.all");
		    	if (entityObject!=null)  {
			    	for (Object oe: entityObject) {
			    		String entityName = oe.toString();
			    		Entity e = new Entity();
			    		e.setName(entityName);
			    		dbentities.add(e);
			    	}
		    	}
		    	
		    	
		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate()!=null && e.getDate().toString().compareTo("2050")<0) { 
		    		//ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		System.out.println("successfully returns " + itemList.size());
	    
		return itemList;
	}
	
	public static void main(String[] args) {
		SolrDBManager sm = new SolrDBManager();
		String query = "*:*";
		List<Event> test_events = sm.searchBySolrQuery(20, query);
		
	}


	public HashMap<String, Integer> getSemanticDistribution(String solrquery, 
			 String language, String field, String filterValue) {
		HashMap<String, Integer> distribution = new HashMap<String, Integer> ();
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();
		query.setFields(field);
//		query.setFields( 
//				"meta.source.headline", 
//				"meta.source.text", 
//				"meta.source.keywords",
//				"meta.extracted.text.dbpedia.all",
//				"meta.extracted.text.dbpedia.PERSON",
//				"meta.extracted.text.dbpedia.ORGANIZATION",
//				"meta.extracted.text.dbpedia.LOCATION",
//				"meta.extracted.text.ner.PERSON",
//				"meta.extracted.text.ner.ORGANIZATION",
//				"meta.extracted.text.ner.LOCATION",
//				"meta.extracted.text.ner.all"
//				);
		query.setQuery(solrquery);
		//query.addFilterQuery("meta.source.inLanguage:\"" + language + "\"");
		query.addFilterQuery(field + ":*" + filterValue + "*");
		query.setRows(1000);
		StoryDistribution sd = new StoryDistribution();
		System.out.println("SearchByKeyword\n" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    
		    	//entity based type
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	String sfield = getFieldFromQuery(field);	//to ensure the corrected field
		    	Collection<Object> entityObject = results.get(i).getFieldValues(sfield);
		    	if (isEntityBasedType(solrquery)) {
			    	if (entityObject!=null)  {
				    	for (Object oe: entityObject) {
				    		String entityName = oe.toString();
				    		int cur = distribution.containsKey(entityName)?distribution.get(entityName):0;
				    		distribution.put(entityName, cur+1);
				    	}
			    	}
		    	}
		    	else {// text base query
			    	if (entityObject!=null)  {
				    	for (Object oe: entityObject) {
				    		String text = oe.toString();
				    		for (String term: text.split("\\s+")) {
				    			term = EventDistribution.truncate(term);
				    			if (term.endsWith(":")) term = term.replace(":","");
				    			if (EventDistribution.isBlackListed(term)) continue;
				    			if (!Stopwords.isStopword(term)) {
				    				int cur = distribution.containsKey(term)?distribution.get(term):0;
				    				distribution.put(term, cur+1);
				    			}
				    		}
				    		
				    	}
			    	}
		    	}
		    
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return distribution;
	}

/**
 * 	
 * @param solrquery
 * @param n: number of nodes to keep
 * @param language
 * @param field
 * @return
 */
	public JSONArray getSemanticGraph(String solrquery, int n, 
			 String language, String field, String filterValue) {
		HashMap<String, Integer> distribution = new HashMap<String, Integer> ();
		HashMap<String, Integer> graph = new HashMap<String, Integer> ();
		SolrQuery query = new SolrQuery();
		query.setFields(field);
//		query.setFields( 
//				"meta.source.headline", 
//				"meta.source.text", 
//				"meta.source.keywords",
//				"meta.extracted.text.dbpedia.all",
//				"meta.extracted.text.dbpedia.PERSON",
//				"meta.extracted.text.dbpedia.ORGANIZATION",
//				"meta.extracted.text.dbpedia.LOCATION",
//				"meta.extracted.text.ner.PERSON",
//				"meta.extracted.text.ner.ORGANIZATION",
//				"meta.extracted.text.ner.LOCATION",
//				"meta.extracted.text.ner.all"
//				);
		query.setQuery(solrquery);
		//query.addFilterQuery("meta.source.inLanguage:\"" + language + "\"");
		query.addFilterQuery(field + ":*" + filterValue + "*");
		query.setRows(1000);
		System.out.println("SearchByKeyword\n" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    
		    	//entity based type
		    	String sfield = getFieldFromQuery(field);	//to ensure the corrected field
		    	Collection<Object> entityObject = results.get(i).getFieldValues(sfield);
		    	HashSet<String> itemset = new HashSet<String> ();
		    	
		    	if (isEntityBasedType(solrquery)) {
			    	if (entityObject!=null)  {
				    	for (Object oe: entityObject) {
				    		String entityName = oe.toString();
				    		int cur = distribution.containsKey(entityName)?distribution.get(entityName):0;
				    		distribution.put(entityName, cur+1);
				    		itemset.add(entityName);
				    	}
				    	
			    	}
		    	}
		    	else {// text base query
			    	if (entityObject!=null)  {
				    	for (Object oe: entityObject) {
				    		String text = oe.toString();
				    		for (String term: text.split("\\s+")) {
				    			term = EventDistribution.truncate(term).toLowerCase();
				    			if (term.endsWith(":")) term = term.replace(":","");
				    			if (EventDistribution.isBlackListed(term)) continue;
				    			if (!Stopwords.isStopword(term)) {
				    				int cur = distribution.containsKey(term)?distribution.get(term):0;
				    				distribution.put(term, cur+1);
				    				itemset.add(term);
				    			}
				    		}
				    		
				    	}
			    	}
		    	}
		    	
		    	//co-occurence
		    	ArrayList<String> entities = new ArrayList<String> ();
		    	for (String s: itemset) entities.add(s);
		    	for (int x = 0; x < entities.size()-1; x++) {
		    		for (int y  = 0; y <x; y++) {
		    			String graphkey = entities.get(y) + ">>_<<" + entities.get(x); 
		    			if (entities.get(x).compareTo(entities.get(y)) <0) {
		    				graphkey = entities.get(x) + ">>_<<" + entities.get(y);
		    			}
		    			int cur = graph.containsKey(graphkey)?graph.get(graphkey):0;
		    			graph.put(graphkey, cur+1);
		    		}
		    	}
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		ArrayList<String> all_items = new ArrayList<String> ();
		for (String s: graph.keySet()) all_items.add(s);
		System.out.println(all_items.size());
		JSONArray jsa = new JSONArray();
		if (all_items.size()==0) return jsa;
		sortingMap.qsort(all_items, graph, 0, all_items.size()-1);
		
		for (int j = 0; j < Math.min(n, graph.size()); j++) {
			JSONObject o = new JSONObject();
			String graphkey = all_items.get(j);
			String[] keysplt  = graphkey.split(">>_<<");
			int f = graph.get(all_items.get(j));
			if  (keysplt.length <2) continue;
			
			try {
				o.put("source", keysplt[0]);
				o.put("target", keysplt[1]);
				o.put("weight", Math.floor(1.0 * f/1));
				jsa.put(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return jsa;
	}
	
	/**
	 * 	 * @param solrquery
	 * @return
	 */
	private boolean isEntityBasedType(String solrquery) {
		String[] fields = new String[] {
				"meta.source.keywords",
				"meta.extracted.text.dbpedia.all",
				"meta.extracted.text.dbpedia.PERSON",
				"meta.extracted.text.dbpedia.ORGANIZATION",
				"meta.extracted.text.dbpedia.LOCATION",
				"meta.extracted.text.ner.PERSON",
				"meta.extracted.text.ner.ORGANIZATION",
				"meta.extracted.text.ner.LOCATION",
				"meta.extracted.text.ner.all"};
		for (String f: fields) 
			if (solrquery.contains(f)) return true;
		return false;
	}
	
	
	private String getFieldFromQuery(String solrquery) {
		String[] fields = new String[] {
				"meta.source.headline", 
				"meta.source.text", 
				"meta.source.keywords",
				"meta.extracted.text.dbpedia.all",
				"meta.extracted.text.dbpedia.PERSON",
				"meta.extracted.text.dbpedia.ORGANIZATION",
				"meta.extracted.text.dbpedia.LOCATION",
				"meta.extracted.text.ner.PERSON",
				"meta.extracted.text.ner.ORGANIZATION",
				"meta.extracted.text.ner.LOCATION",
				"meta.extracted.text.ner.all"};
		for (String f: fields) 
			if (solrquery.contains(f)) return f;
		return "meta.extracted.text.ner.all"; // by default
	}
	
}
