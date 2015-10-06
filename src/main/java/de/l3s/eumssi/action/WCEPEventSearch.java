package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

import de.l3s.eumssi.dao.DatabaseManager;
import de.l3s.eumssi.model.*;
import de.l3s.eumssi.service.ContentHandling;

public class WCEPEventSearch  extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
	private ContentHandling helper = new ContentHandling();
	private DatabaseManager db = new DatabaseManager();
	private String query;
	private String filterType = null;
	private String filterItemId = null;
	private int searchway;
	private boolean infer;
	
	private Object contextPath;
	private boolean useContextPath;
	private String location;

	private String itemType;
	private String itemId;
	private String itemName;
	private int searchsize;
	private String fromDate;
	private String toDate;
	private boolean hasWikipediaUrl;
	private String wikipediaUrl;
	private boolean usingTimeWindow;
	
	private JSONObject timeline;
	
	private List<Category> relatedCategories = null;
	private List<Location> relatedLocations = null;
	private List<Story> relatedStories=null;
	private List<Story> stories=null;
	private List<Event> events;
	private List<Entity> entities = null;
	private List<Entity> topEntities = null;
	
	private javax.servlet.http.HttpServletRequest request;
	
	@Override
	 public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
	
	
	
	public String eventSearch() throws Exception {
		if (ServletActionContext.getRequest().getServerName().equals(db.conf.getProperty("domainName"))){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		infer = Boolean.valueOf(db.conf.getProperty("display_infered_relations"));
		
		
		try{
		
			itemType = "Query";
			itemId = null;
			if(query!=null){
				itemName = query;
			}else{
				itemName = " show all events between " + fromDate + " and " + toDate;
			}
			
			hasWikipediaUrl = false;
			wikipediaUrl = "";
	
			List<Event> eventsTmp = new ArrayList<Event>();
			if(query == null){
				eventsTmp = db.getEvents(fromDate, toDate);
			}else if (query.isEmpty()){
				eventsTmp = db.getEvents(fromDate, toDate);
			}else{
				eventsTmp = db.searchEventsByKeyword(query, fromDate, toDate);
			}

			
/*			if (infer) 
				events_tmp = helper.addInferedInformation(events_tmp);
*/			
			// apply filters, if exist (e.g. show only events from a specific story, category, or entity)
			if(filterType != null && filterItemId != null){
				events = helper.filterEvents(eventsTmp, filterType, filterItemId);	
			}else{
				events = eventsTmp;
			}
							
			searchsize = events.size();
					
			
			// 1. add the links for each entity mention
			// and remove double montion of references
			for (Event e: events){
//				if (e.getAnnotatedDescription() != null)
					e = helper.addEntityLinks(e, contextPath);
//				else
//					e = helper.addEntityLinks_Old(e, contextPath);
				
//				e = helper.removeDoubleReferences(e);
			}
			
			
			
			// reverse the order of events to show latest events first
			Collections.sort(events, Collections.reverseOrder());

			// get the dates of the first and last events to show on results page:
			if (!events.isEmpty()){
				toDate = events.get(0).getDate().toString();
				fromDate = events.get(events.size()-1).getDate().toString();
			}			
			
			
			int maxItems = Integer.valueOf(db.conf.getProperty("maxItemsInLeftSide"));
			
			// 2. get related categories
			relatedCategories = helper.getCategoryList(events);
//			if(relatedCategories.size() > maxItems)
//				relatedCategories = (ArrayList<Category>) relatedCategories.subList(0, maxItems-1);
			
			// 3. get related stories
			relatedStories = helper.getStoryList(events);
//			if(relatedStories.size() > maxItems)
//				relatedStories = (ArrayList<Story>) relatedStories.subList(0, maxItems-1);
			
			//4. get top entities
			topEntities = helper.getEntities(events, maxItems);
			
			// 5. get related locations
			relatedLocations = helper.getLocationList(events);
			
			
			// For performance purpose, only show the last x events:
			//
			int maxNumOfEventsToDisplay = Integer.parseInt(db.conf.getProperty("visualization_MaxTimelineSize"));
			if (events.size() > maxNumOfEventsToDisplay ){
				List<Event> eventsToDisplay = new ArrayList<>();
				for(int i=0; i< maxNumOfEventsToDisplay; i++){
					eventsToDisplay.add(new Event (events.get(i)));
				}
				events = eventsToDisplay;
			}
			timeline = helper.getTimelineJSON(events, contextPath);
			
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
			
		return "QueryTimelineView";
	}
	
	
	
	
	
	



	public String storySearch() throws Exception {
		if ("wikitimes.l3s.de".equals(ServletActionContext.getRequest().getServerName())){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
			
			if(query == null){
				stories = helper.getStoryList(db.getEvents(fromDate, toDate));
			}else if (query.isEmpty()){
				stories = helper.getStoryList(db.getEvents(fromDate, toDate));
			}else{
				stories = helper.getStoryList(db.searchEventsByKeyword(query, fromDate, toDate));
			}
			
/*			if(query == null)
				stories = helper.getStoryList(db.getEvents(fromDate,toDate));	
			else
				stories = helper.getStoryList(db.searchEventsByKeyword(query, fromDate, toDate));
*/			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
		
		return "StoryListView";
	}
	
	
	public String storySearchByName() throws Exception {
		if ("wikitimes.l3s.de".equals(ServletActionContext.getRequest().getServerName())){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
			stories = db.searchStoriesByName(query, fromDate, toDate);
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
		
		return "StoryListView";
	}
	
	
	
	
	public String entitySearch() {
		if ("wikitimes.l3s.de".equals(ServletActionContext.getRequest().getServerName())){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
			if(query == null){
				setEntities(helper.getEntities(db.getEvents(fromDate,toDate), -1));	
			}else if (query.isEmpty()){
				setEntities(helper.getEntities(db.getEvents(fromDate,toDate), -1));	
			}else{
				setEntities(helper.getEntities(db.searchEventsByKeyword(query, fromDate, toDate), -1));
			}
/*			
			if(query == null)
				setEntities(helper.getEntities(db.getEvents(fromDate,toDate), -1));	
			else
				setEntities(helper.getEntities(db.searchEventsByKeyword(query, fromDate, toDate), -1));
*/		
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
		
		return "EntityListView";
	}
	

	public String entitySearchByName() {
		if ("wikitimes.l3s.de".equals(ServletActionContext.getRequest().getServerName())){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
			entities = db.searchEntitiesByName(query, fromDate, toDate);
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
		
		return "EntityListView";
	}
	
	
	
	
	
	
	public String getQuery() {
		return query;
	}


	public void setQuery(String query) {
		this.query = query;
	}


	
	
	
	
	public String getFilterType() {
		return filterType;
	}



	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}



	public String getFilterItemId() {
		return filterItemId;
	}



	public void setFilterItemId(String filterItemId) {
		this.filterItemId = filterItemId;
	}



	public int getSearchway() {
		return searchway;
	}


	public void setSearchway(int searchway) {
		this.searchway = searchway;
	}

	public int getSearchsize() {
		return searchsize;
	}



	public void setSearchsize(int searchsize) {
		this.searchsize = searchsize;
	}



	public List<Category> getRelatedCategories() {
		return relatedCategories;
	}



	public void setRelatedCategories(List<Category> categories) {
		this.relatedCategories = categories;
	}

	public List<Story> getRelatedStories() {
		return relatedStories;
	}

	public void setRelatedStories(List<Story> mystories) {
		this.relatedStories = mystories;
	}

	public List<Story> getStories() {
		return stories;
	}

	public void setStories(List<Story> stories) {
		this.stories = stories;
	}

	public javax.servlet.http.HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(javax.servlet.http.HttpServletRequest request) {
		this.request = request;
	}

	public List<Event> getEvents() {
		return events;
	}



	public void setEvents(List<Event> events) {
		this.events = events;
	}



	public String getFromDate() {
		return fromDate;
	}



	public void setFromDate(String qfromDate) {
		this.fromDate = qfromDate;
	}



	public String getToDate() {
		return toDate;
	}



	public void setToDate(String qtoDate) {
		this.toDate = qtoDate;
	}

	public Object getContextPath() {
		return contextPath;
	}

	public void setContextPath(Object contextPath) {
		this.contextPath = contextPath;
	}

	public boolean isUseContextPath() {
		return useContextPath;
	}

	public void setUseContextPath(boolean useContextPath) {
		this.useContextPath = useContextPath;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public List<Entity> getTopEntities() {
		return topEntities;
	}

	public void setTopEntities(List<Entity> topentity) {
		this.topEntities = topentity;
	}

	public boolean isHasWikipediaUrl() {
		return hasWikipediaUrl;
	}

	public void setHasWikipediaUrl(boolean hasWikipediaUrl) {
		this.hasWikipediaUrl = hasWikipediaUrl;
	}

	public String getWikipediaUrl() {
		return wikipediaUrl;
	}

	public void setWikipediaUrl(String wikipediaUrl) {
		this.wikipediaUrl = wikipediaUrl;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}



	public List<Entity> getEntities() {
		return entities;
	}



	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}



	public String getItemId() {
		return itemId;
	}



	public void setItemId(String itemId) {
		this.itemId = itemId;
	}



	public boolean getInfer() {
		return infer;
	}



	public void setInfer(boolean infer) {
		this.infer = infer;
	}



	public JSONObject getTimeline() {
		return timeline;
	}



	public void setTimeline(JSONObject timeline) {
		this.timeline = timeline;
	}



	public boolean isUsingTimeWindow() {
		return usingTimeWindow;
	}



	public void setUsingTimeWindow(boolean usingTimeWindow) {
		this.usingTimeWindow = usingTimeWindow;
	}



	public List<Location> getRelatedLocations() {
		return relatedLocations;
	}



	public void setRelatedLocations(List<Location> relatedLocations) {
		this.relatedLocations = relatedLocations;
	}



}
