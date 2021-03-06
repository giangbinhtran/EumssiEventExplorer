package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;

import com.opensymphony.xwork2.Action;

import de.l3s.eumssi.dao.DatabaseManager;
import de.l3s.eumssi.model.Category;
import de.l3s.eumssi.model.Entity;
import de.l3s.eumssi.model.Event;
import de.l3s.eumssi.model.Location;
import de.l3s.eumssi.model.Story;
import de.l3s.eumssi.service.ContentHandling;

public class StoryTimelineAction implements Action{
	
	private Story story;
	
	private int storyId;
	
	private ContentHandling helper = new ContentHandling();
	private DatabaseManager db = new DatabaseManager();
	
	private String filterType = null;
	private String filterItemId = null;
	private String itemType;
	private String itemId;
	private String itemName;
	private int searchsize;
	private String fromDate;
	private String toDate;
	private boolean hasWikipediaUrl;
	private String wikipediaUrl;
	
	private JSONObject timeline;
	
	private ArrayList<Event> events=null;
	private ArrayList<Category> relatedCategories=null;
	private ArrayList<Location> relatedLocations = null;
	private ArrayList<Story> relatedStories=null;
	private ArrayList<Entity> topEntities = null;
	
	private Object contextPath;
	private boolean useContextPath;
	
	
	@Override
	public String execute() throws Exception {
		
		if (ServletActionContext.getRequest().getServerName().equals("wikitimes.l3s.de")){
			contextPath = null;
			useContextPath = false;
		}else{
			contextPath = ServletActionContext.getServletContext().getContextPath();
			useContextPath = true;
		}
		
		try{
			
			story = db.getStoryById(storyId+"");
			
			// apply filters, if exist (e.g. show only events from a specific story, category, or entity)
			if(filterType != null && filterItemId != null){
				events = helper.filterEvents(db.getEventsByStory(storyId+""), filterType, filterItemId);	
			}else{
				events = db.getEventsByStory(storyId+"");
			}

			// 1. add the links for each entity mention
			for (Event e: events){
				e = helper.addEntityLinks(e, contextPath);	        			
			}
			
			
			//For visualization purpose:
			setTimeline(helper.getTimelineJSON(events, contextPath));
			
			// reverse the order of events to show latest events first
			Collections.sort(events, Collections.reverseOrder());
			
			searchsize = events.size();
			setItemType("News Story");
			setItemId(story.getId());
			setItemName(story.getName());
			setHasWikipediaUrl(true);
			setWikipediaUrl(story.getWikipediaUrl());
			
			// get the dates of the first and last events to show on results page:
			toDate = events.get(0).getDate().toString();
			fromDate = events.get(events.size()-1).getDate().toString();
			
			

						
			// 2. get related categories
			setRelatedCategories(helper.getCategoryList(events));
					
			// 3. get related stories
//			setRelatedStories(helper.getStoryList(events));
			relatedStories = db.getRelatedStories(storyId+"");
			
			//4. get top entities
			setTopEntities(helper.getEntities(events, 20));
			
			// 5. get related locations
			relatedLocations = helper.getLocationList(events);
		
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			db.closeConnection();
		}
		
		return "StoryTimelineView";
	}

	
	
	
	public int getStoryId() {
		return storyId;
	}

	public void setStoryId(int storyId) {
		this.storyId = storyId;
	}

	public ContentHandling getHelper() {
		return helper;
	}

	public void setHelper(ContentHandling helper) {
		this.helper = helper;
	}



	

	public int getSearchsize() {
		return searchsize;
	}

	public void setSearchsize(int resultsize) {
		this.searchsize = resultsize;
	}


	public ArrayList<Category> getRelatedCategories() {
		return relatedCategories;
	}

	public void setRelatedCategories(ArrayList<Category> categories) {
		this.relatedCategories = categories;
	}

	public ArrayList<Story> getRelatedStories() {
		return relatedStories;
	}

	public void setRelatedStories(ArrayList<Story> stories) {
		this.relatedStories = stories;
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





	public Story getStory() {
		return story;
	}





	public void setStory(Story story) {
		this.story = story;
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




	public String getWikipediaUrl() {
		return wikipediaUrl;
	}





	public void setWikipediaUrl(String wikipediaUrl) {
		this.wikipediaUrl = wikipediaUrl;
	}





	public ArrayList<Entity> getTopEntities() {
		return topEntities;
	}





	public void setTopEntities(ArrayList<Entity> topEntities) {
		this.topEntities = topEntities;
	}




	public String getItemId() {
		return itemId;
	}




	public void setItemId(String itemId) {
		this.itemId = itemId;
	}




	public JSONObject getTimeline() {
		return timeline;
	}




	public void setTimeline(JSONObject timeline) {
		this.timeline = timeline;
	}




	public ArrayList<Location> getRelatedLocations() {
		return relatedLocations;
	}




	public void setRelatedLocations(ArrayList<Location> relatedLocations) {
		this.relatedLocations = relatedLocations;
	}
	
	
}
