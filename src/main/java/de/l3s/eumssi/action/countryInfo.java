package de.l3s.eumssi.action;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opensymphony.xwork2.Action;

public class countryInfo implements Action, ServletRequestAware {
	 
	JSONObject objWriterOuter = new JSONObject();
	JSONObject objWriterInner = new JSONObject();
	HttpServletRequest request; 
	public String execute() throws JSONException{
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(path+"scripts\\country.json"));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray countryArray =  (JSONArray)jsonObject.get("results");
			for( int i=0;i<countryArray.size(); i++){
				JSONObject jsonObject1;
				jsonObject1=(JSONObject) countryArray.get(i);
				JSONObject jsonObjectCountry=(JSONObject) jsonObject1.get("country");
				String countryUrl=(String) jsonObjectCountry.get("value");
				 String[] splitKey=(String[])countryUrl.split("/");
  			    String countryName=splitKey[splitKey.length-1].toString();
  			  System.out.println(countryName);
  			    
  			  JSONObject jsonObjectCapital=(JSONObject) jsonObject1.get("capital");
				String capitalUrl=(String) jsonObjectCapital.get("value");
				System.out.println(capitalUrl);
				 String[] splitKey1=(String[])capitalUrl.split("/");
	  			    String capitalName=splitKey1[splitKey1.length-1].toString();
	  			  System.out.println(capitalName);
	  			    
				JSONObject jsonObjectCurrency=(JSONObject) jsonObject1.get("currencyname");
				String currencyName=(String) jsonObjectCurrency.get("value");
				System.out.println(currencyName);
				
				
				JSONObject jsonObjectLanguage=(JSONObject) jsonObject1.get("languagename");
				String languageName=(String) jsonObjectLanguage.get("value");
				
				System.out.println(languageName);
				
				JSONObject jsonObjectLat=(JSONObject) jsonObject1.get("lat");
				String lat=(String) jsonObjectLat.get("value");
				
				JSONObject jsonObjectLong=(JSONObject) jsonObject1.get("long");
				String longi=(String) jsonObjectLong.get("value");
  		
				JSONObject objWriterInner = new JSONObject();
				
				objWriterInner.put("capital", capitalName);
				objWriterInner.put("currency", currencyName);
				objWriterInner.put("language", languageName);
				objWriterInner.put("lat", lat);
				objWriterInner.put("long", longi);
		        
				objWriterOuter.put(countryName, objWriterInner);
				
				
			}
			FileWriter file = new FileWriter(path+"scripts\\countryinfo.json");
  			file.write(objWriterOuter.toJSONString());
			
			
			file.flush();
			file.close();
		} catch (FileNotFoundException e) {
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

}
