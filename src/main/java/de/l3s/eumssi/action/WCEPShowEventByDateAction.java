package de.l3s.eumssi.action;

import java.util.ArrayList;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;

import de.l3s.eumssi.dao.DatabaseManager;
import de.l3s.eumssi.model.Category;
import de.l3s.eumssi.model.Entity;
import de.l3s.eumssi.model.Event;
import de.l3s.eumssi.model.Location;
import de.l3s.eumssi.model.Story;
import de.l3s.eumssi.service.ContentHandling;

public class WCEPShowEventByDateAction implements Action{
	
	private ContentHandling helper = new ContentHandling();
	private DatabaseManager db = new DatabaseManager();
	
	private String storyDate;
	private ArrayList<Event> events=new ArrayList<Event>();
	private Object contextPath;
	private boolean useContextPath;
	
	private String itemType;
	private String itemName;
	private int searchsize;
	private String fromDate;
	private String toDate;
	private boolean hasWikipediaUrl;
	private String wikipediaUrl;
	
	private ArrayList<Category> relatedCategories;
	private ArrayList<Story> relatedStories;
	private ArrayList<Entity> topEntities = null;
	private ArrayList<Location> relatedLocations = null;
	
	public ArrayList<Location> getRelatedLocations() {
		return relatedLocations;
	}

	public void setRelatedLocations(ArrayList<Location> relatedLocations) {
		this.relatedLocations = relatedLocations;
	}




	@Override
	public String execute() throws Exception {
			
		if (ServletActionContext.getRequest().getServerName().equals("eumssi.l3s.de")){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
		
			itemType = "Events of the Day";
			itemName = storyDate;
			hasWikipediaUrl = false;
			wikipediaUrl = "";
			fromDate = storyDate;
			toDate = storyDate;
			
			events = db.getEventsByDate(storyDate);
			
			searchsize = events.size();
			
			// prepare the output:
			// 1. add the links for each entity mention
			for (Event e: events){
				e = helper.addEntityLinks(e, contextPath);	        			
			}
						
			// 2. get related categories
			relatedCategories = helper.getCategoryList(events);
					
			// 3. get related stories
			relatedStories = helper.getStoryList(events);
			
			//4. get top entities
			setTopEntities(helper.getEntities(events, 20));
			
			//5. get relatedLocations
			setRelatedLocations(helper.getLocationList(events));
			System.out.println("Number of locations to be: " + relatedLocations.size());
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeConnection();
		}
					
		return "WCEPTimelineView";
	}

	
	
	
	public ContentHandling getHelper() {
		return helper;
	}

	public void setHelper(ContentHandling helper) {
		this.helper = helper;
	}

	public String getStoryDate() {
		return storyDate;
	}

	public void setStoryDate(String storyDate) {
		this.storyDate = storyDate;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
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

	public ArrayList<Category> getRelatedCategories() {
		return relatedCategories;
	}

	public void setRelatedCategories(ArrayList<Category> relatedCategories) {
		this.relatedCategories = relatedCategories;
	}

	public ArrayList<Story> getRelatedStories() {
		return relatedStories;
	}

	public void setRelatedStories(ArrayList<Story> relatedStories) {
		this.relatedStories = relatedStories;
	}

	public ArrayList<Entity> getTopEntities() {
		return topEntities;
	}

	public void setTopEntities(ArrayList<Entity> topEntities) {
		this.topEntities = topEntities;
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

	public int getSearchsize() {
		return searchsize;
	}

	public void setSearchsize(int searchsize) {
		this.searchsize = searchsize;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
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
	
	
}
