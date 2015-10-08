package de.l3s.eumssi.action;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opensymphony.xwork2.Action;

public class countryQuestionAction implements Action, ServletRequestAware {
	 private ArrayList countryNames=new ArrayList(); 
     private String submittedCountryName;
     public ArrayList distances=new ArrayList();
     public Map distanceToCountry=new HashMap();
     public Map neighbor1=new HashMap();
     public Map neighbor2=new HashMap();
     public Map neighbor3=new HashMap();
     public String neighborCountry1;
     public String neighborCountry2;
     public String neighborCountry3;
	HttpServletRequest request;
	
	private double getDistance(double submittedCountryLat,double submittedCountryLong,double countryLat,double countryLong){

		double distance = 0;
		double submittedCountryLatRadian=Math.toRadians(submittedCountryLat);		
		double submittedCountryLongRadian=Math.toRadians(submittedCountryLong);
		double countryLatRadian=Math.toRadians(countryLat);
		double countryLongRadian=Math.toRadians(countryLong);
		
		double absoluteDistanceLat=submittedCountryLatRadian-countryLatRadian;
		double absoluteDistanceLong=submittedCountryLongRadian-countryLongRadian;
		
		distance=Math.sin(absoluteDistanceLat/2)*Math.sin(absoluteDistanceLat/2) +
				Math.cos(submittedCountryLatRadian)*Math.cos(countryLatRadian)* Math.sin(absoluteDistanceLong/2)*Math.sin(absoluteDistanceLong/2);
		  
		distance=2* Math.asin(Math.sqrt(distance));
		return distance*6371;
		
	}
	
	public String execute() throws JSONException{
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			obj = parser.parse(new FileReader(path+"scripts\\countryinfo.json"));
			JSONObject jsonObject = (JSONObject) obj;
		  	 for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
     		    String key = (String) iterator.next();
		  	    countryNames.add(key);
		  	    
		  	 }
		  	 if(submittedCountryName!=null){
		  JSONObject submittedCountryJsonObject=(JSONObject)jsonObject.get(submittedCountryName);
		
		  double submittedCountryLat=Double.valueOf((String) submittedCountryJsonObject.get("lat"));
		  double submittedCountryLong=Double.valueOf((String)submittedCountryJsonObject.get("long"));
		  	
		  for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
   		    String countryName = (String) iterator.next();
		  	   if(countryName.equals(submittedCountryName)){
		  		   continue;
		  	   }
		  	   else{
		  		 JSONObject countryJsonObject=(JSONObject)jsonObject.get(countryName);
		  		 double countryLat=Double.valueOf((String) countryJsonObject.get("lat"));
				 double countryLong=Double.valueOf((String) countryJsonObject.get("long"));
				  double distance= getDistance(submittedCountryLat,submittedCountryLong,countryLat,countryLong);				  
				  distances.add(distance);
				  distanceToCountry.put(distance,countryName);
				 
		  	   }
		  	    
		  	 }
		   
		  Collections.sort(distances);
		  neighborCountry1=(String)distanceToCountry.get(distances.get(0));
		  neighborCountry2=(String)distanceToCountry.get(distances.get(1));
		  neighborCountry3=(String)distanceToCountry.get(distances.get(2));
         
       JSONObject neighborJsonObect1=(JSONObject) jsonObject.get(distanceToCountry.get(distances.get(0)));
       JSONObject neighborJsonObect2=(JSONObject) jsonObject.get(distanceToCountry.get(distances.get(1)));
       JSONObject neighborJsonObect3=(JSONObject) jsonObject.get(distanceToCountry.get(distances.get(2)));
       neighbor1.put("capital", neighborJsonObect1.get("capital"));
       neighbor1.put("currency", neighborJsonObect1.get("currency"));
       neighbor1.put("language", neighborJsonObect1.get("language"));
       
       neighbor2.put("capital", neighborJsonObect2.get("capital"));
       neighbor2.put("currency", neighborJsonObect2.get("currency"));
       neighbor2.put("language", neighborJsonObect2.get("language"));
       
       neighbor3.put("capital", neighborJsonObect3.get("capital"));
       neighbor3.put("currency", neighborJsonObect3.get("currency"));
       neighbor3.put("language", neighborJsonObect3.get("language"));
       
         System.out.println(neighbor1);
		  	 }
		} 
         		
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
				return "success";
	}
	
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		 this.request = request; 
		
	}



	public ArrayList getCountryNames() {
		return countryNames;
	}



	public void setCountryNames(ArrayList countryNames) {
		this.countryNames = countryNames;
	}



	public String getSubmittedCountryName() {
		return submittedCountryName;
	}



	public void setSubmittedCountryName(String submittedCountryName) {
		this.submittedCountryName = submittedCountryName;
	}
	
	public Map getNeighbor1() {
		return neighbor1;
	}
	
	public Map getNeighbor2() {
		return neighbor2;
	}
	
	public Map getNeighbor3() {
		return neighbor3;
	}
	
	public String getNeighborCountry1() {
		return neighborCountry1;
	}
	
	public String getNeighborCountry2() {
		return neighborCountry2;
	}
	
	public String getNeighborCountry3() {
		return neighborCountry3;
	}

}
