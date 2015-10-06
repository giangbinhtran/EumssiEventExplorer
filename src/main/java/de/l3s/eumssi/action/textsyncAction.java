package de.l3s.eumssi.action;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.opensymphony.xwork2.Action;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class textsyncAction implements Action, ServletRequestAware{
	private String myparam;
	private String url;
	public String jsonFileName;
   public	String entities[];
   public int hour=00;
   public int min=00;
   public int sec=00;
    public String dataContent=null;	
	public String content1 = "{\"localisation\": [{ \"sublocalisations\": { \"localisation\": [";
    public String content8="]},\"type\": \"text\",\"tcin\": \"00:00:00.0000\",\"tcout\": \"02:00:00.0000\",\"tclevel\": 0}],\"id\": \"text-amalia01\",\"type\": \"text\",\"algorithm\": \"demo-video-generator\",\"processor\": \"Ina Research Department - N. HERVE\", \"processed\": 1421141589288, \"version\": 1}";
	public ArrayList<ArrayList<ArrayList<String>>> mainArrayList = new ArrayList<ArrayList<ArrayList<String>>>(); //3d Array for holding all the property-value pair of entities.
	 public ArrayList<String> thumbNailsList= new ArrayList<String>(); 
		HttpServletRequest request; 
	 // Function for reading dbpedia json file
	private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read); 
            }
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
	
	private void reArrangeMainArrayList(ArrayList<ArrayList<ArrayList<String>>> mainArrayList){
		 for(int i=0;i<mainArrayList.size();i++){
		      for(int j=0;j<mainArrayList.get(i).size();j++)
					 { 
					   String key=mainArrayList.get(i).get(j).get(0);
					   if(key.equals("knownFor")){
						   for(int k=j+1;k<mainArrayList.get(i).size();k++){
							   if(mainArrayList.get(i).get(k).get(0).equals("knownFor")){
								   String concat=mainArrayList.get(i).get(j).get(1)+", "+mainArrayList.get(i).get(k).get(1);
								   mainArrayList.get(i).remove(k);
								   mainArrayList.get(i).get(j).set(1, concat);
							   }
						   }
						   break;   
					   }
					   
					 }
	}
		// System.out.println(mainArrayList);
	}
	private String findingAbstract(ArrayList<ArrayList<ArrayList<String>>> mainArrayList, int entityNumber){
		String[] triviaDemo;
		String[] trivia=new String[10];
        String key;	
		  for(int i=entityNumber;i<entityNumber+1;i++){
		     for(int j=0;j<mainArrayList.get(i).size();j++)
					 { 
					   key= mainArrayList.get(i).get(j).get(0);
					  // System.out.println(key);
			
					   if(key.equals("abstract"))
						   
					   {
						    triviaDemo=mainArrayList.get(i).get(j).get(1).split("\\.");
						   
						 
						
						    trivia[i]=triviaDemo[0];
						    
						    
						    break;
					   }
					 }
		   
		  }
		  
		  return trivia[entityNumber];	
		  }
	
	private void makeData(String data, int dataCounter, String thumbNailUrl ){
		 String content2= "{\"data\": {\"text\": [";
         String content3="]},";
         String content4="\"tcin\":";
         String content5=",\"tcout\":";
         
         String content6=",\"tclevel\": 1}";
         String content7=",";
         String dataContentDemo=null;
         String tcin;
		 String tcout;
		 
		
		 if(sec==60){
			  sec=00;
			  min=min+1;
			  }
		  else
			  sec=sec+30;
		  if(min==60){
			  sec=00;
		      min=00;
		      hour=hour+1;
		   }
		  tcin=new DecimalFormat("00").format(hour)+":"+new DecimalFormat("00").format(min)+":"+new DecimalFormat("00").format(sec)+"."+"0000";
		  //tcin=hour+":"+min+":"+sec+"."+"0000";
		  if(sec<60)
		  sec=sec+10;
		  else{
			  sec=00;
			  min=min+1;
		  }
		  tcout=new DecimalFormat("00").format(hour)+":"+new DecimalFormat("00").format(min)+":"+new DecimalFormat("00").format(sec)+"."+"0000";
		  //System.out.println(data);
		 
		  if(dataCounter>1){
		      dataContentDemo=content7+content2+"\""+data+"\""+content3+content4+"\""+tcin+"\""+content5+"\""+tcout+"\""+",\"thumb\""+":"+"\""+thumbNailUrl+"\""+content6;
			  dataContent=dataContent+dataContentDemo;
		  }
		  else
		  {
		  dataContentDemo=content2+"\""+data+"\""+content3+content4+"\""+tcin+"\""+content5+"\""+tcout+"\""+",\"thumb\""+":"+"\""+thumbNailUrl+"\""+content6;
		  dataContent=dataContentDemo;
		  }
		 
		
	}
	//Function for writting to json file for Amalia player
	
	private void jsonWriter(  ArrayList<ArrayList<ArrayList<String>>> mainArrayList){
		String mainContent; 
		
		
		Map<String,ArrayList> mapQuestion =new HashMap<String,ArrayList>();
		Map<String,String> mapInfo =new HashMap<String,String>();
		Map<String,String> mapTrivia =new HashMap<String,String>();
		
		List currencyOptions=new ArrayList(Arrays.asList(new String[] {"Euro","Dollar","Taka","Rupi"}));
		mapQuestion.put("currency",new ArrayList());
		mapQuestion.get("currency").add("What is the name of the currency?");
		mapQuestion.get("currency").add(currencyOptions);
		
		List capitalOptions=new ArrayList(Arrays.asList(new String[] {"Dhaka","London","Mexico_City","Beijing"}));
		mapQuestion.put("capital",new ArrayList());
		mapQuestion.get("capital").add("What is the name of the capital?");
		mapQuestion.get("capital").add(capitalOptions);
		
		List officialLanguageOptions=new ArrayList(Arrays.asList(new String[] {"English","Spanish","Bengali","Hindi"}));
		mapQuestion.put("officialLanguage",new ArrayList());
		mapQuestion.get("officialLanguage").add("What is the name of the officialLanguage?");
		mapQuestion.get("officialLanguage").add(officialLanguageOptions);	
				
		//	mapQuestion.put("birthDate", "Do you know the birhdate?");
	//	mapQuestion.put("birthPlace", "do you know the bithplace?");
//		mapQuestion.put("almaMater", "Which university he/she attended?");
	//	mapQuestion.put("founder", "Do you know who is the founder?");
	//	mapQuestion.put("products", "What are the main products?");
	//	mapQuestion.put("foundingYear", "When it was founded?");
        mapInfo.put("homepage", "The homepage is: ");
        mapInfo.put("capital", "The capital is ");
        mapInfo.put("officialLanguage", "The official language is ");
        mapInfo.put("birthPlace", "The place of birth is ");
        mapInfo.put("almaMater", "Attended ");
        mapInfo.put("founder", "name of the founder is ");
        mapTrivia.put("knownFor", "known for");
        
        
		
		
		int sizeOfArrayList=0;
		
		for(int i=0;i<mainArrayList.size();i++)
			{
				sizeOfArrayList=sizeOfArrayList+mainArrayList.get(i).size();
			}
		
	    for(int m=0;m<mainArrayList.size();m++){
	     for(int n=0;n<mainArrayList.get(m).size();n++)
				 { if(mainArrayList.get(m).get(n).get(0).equals("thumbnail"))
					 thumbNailsList.add(mainArrayList.get(m).get(n).get(1));
			
				 }
	     
	    }
		
		try {
			String data;
     		String key;
		    String value;
		    int  dataCounter=0;
		    ArrayList question=null;
		    String info=null;
		    String trivia=null;
		  //  System.out.println(mainArrayList);
		    for(int i=0;i<mainArrayList.size();i++){
		       /*  data="Abstract"+":"+"<br>"+findingAbstract(mainArrayList, i);
		         dataCounter++;
		         makeData(data, dataCounter,thumbNailsList.get(i));
		        */
		    	Map<String,String> questionMap =new HashMap<String,String>();
				Map<String,String> infoMap =new HashMap<String,String>();
				Map<String,String> triviaMap =new HashMap<String,String>();
				List alreadyHappenedQuestion=new ArrayList();
				List alreadyHappenedInfo=new ArrayList();
				for(int j=0;j<mainArrayList.get(i).size();j++)
					 { 
					   key= mainArrayList.get(i).get(j).get(0);
					   question=mapQuestion.get(key);
					  
					   if(question!=null && !alreadyHappenedQuestion.contains(key))
					   {
						   alreadyHappenedQuestion.add(key);
					   }
					   else
						   question=null;
					   info=mapInfo.get(key);
					   if(info!=null && !alreadyHappenedInfo.contains(key))
					   {
						   alreadyHappenedInfo.add(key);
					   }
					   else
						   info=null;
					   
					   trivia=mapTrivia.get(key);
					/*	   
					   Random ran = new Random();
					   int x = ran.nextInt(3) + 1;
					   System.out.println(x);
					  
						    */
					   if(question!=null || info!=null || trivia!=null){
						  value=mainArrayList.get(i).get(j).get(1);
						  value=value.replaceAll("\"","");
						  if(question!=null){
							  
							  if(!((ArrayList)question.get(1)).contains(value)){
								  Random ran = new Random();
								   int x = ran.nextInt(3) + 1; 
								   ((ArrayList)question.get(1)).remove(x);
								   ((ArrayList)question.get(1)).add(x, value);
							  }
							  String options=null;
							  for(int optionCounter=0;optionCounter<=3;optionCounter++){
							       if(options==null){
							    	   options="<input type='radio' name=\'"+value+"\' value=\'"+((ArrayList)question.get(1)).get(optionCounter)+"\'>"+((ArrayList)question.get(1)).get(optionCounter);
							       }	
							       else
								  options=options+"<input type='radio' name=\'"+value+"\' value=\'"+((ArrayList)question.get(1)).get(optionCounter)+"\'>"+((ArrayList)question.get(1)).get(optionCounter);
							  }
							  
							  String temp=entities[i+1]+":"+"<br>"+question.get(0)+":"+"<br>Answer: <div>"+options+"<br><input type='button' value='check'>"+"</div>";
							  questionMap.put(key, temp);
						  }
						  
						  if(info!=null){
							  String temp=entities[i+1]+":"+"<br>"+info+":"+value;
							  infoMap.put(key, temp);
						  }
						  if(trivia!=null){
							  String temp=entities[i+1]+":"+"<br>"+trivia+":"+value;
							  triviaMap.put(key, temp);
						  }
						 
						 
					   } 
						
						   
                       //     dataCounter++; 
                        //    makeData(data,dataCounter,thumbNailsList.get(i));
					 	  
					 }
					  
				//	  System.out.println(infoMap);
				//	  System.out.println(triviaMap);
					  Random ran = new Random();
					  int questionOrInfo=0;
					  if(!questionMap.isEmpty() && !infoMap.isEmpty())
						   questionOrInfo = ran.nextInt(2) + 1;
					  else if(!questionMap.isEmpty())
						  questionOrInfo=1;
					  else if(!infoMap.isEmpty())
						  questionOrInfo=2;
					  if(questionOrInfo==1){
						  int size= questionMap.size();
						  int questionNumber=ran.nextInt(size)+1;
						  int counter=0;
						  Iterator it = questionMap.entrySet().iterator();
						    while (it.hasNext()) {
						       counter++;
						    	Map.Entry pair = (Map.Entry)it.next();
						    	if(counter==questionNumber){
						    	data=(String)pair.getValue();
						    	dataCounter++;
						    	System.out.println(data);
						    	makeData(data,dataCounter,thumbNailsList.get(i));
						    	break;
						    	}
						    	}
					  }
					  if(questionOrInfo==2){
						  int size= infoMap.size();
						  int infoNumber=ran.nextInt(size)+1;
						  int counter=0;
						  Iterator it = infoMap.entrySet().iterator();
						    while (it.hasNext()) {
						       counter++;
						    	Map.Entry pair = (Map.Entry)it.next();
						    	if(counter==infoNumber){
						    	data=(String)pair.getValue();
						    	dataCounter++;
						    	makeData(data,dataCounter,thumbNailsList.get(i));
						    	break;
						    	}
						    	}
						  
					  }
					  int abstractOrTrivia;
					  if(triviaMap.isEmpty())
						  abstractOrTrivia=1;
					  else
					     abstractOrTrivia = ran.nextInt(2) + 1;
					  if(abstractOrTrivia==1){
						  data="Abstract"+":"+"&lt;br&gt;"+findingAbstract(mainArrayList, i);
						  dataCounter++;
						  makeData(data,dataCounter,thumbNailsList.get(i));
					  }
					  if(abstractOrTrivia==2){
						  data=triviaMap.get("knownFor");
						  dataCounter++;
						  makeData(data,dataCounter,thumbNailsList.get(i));
					  }
					  
					 }
		
		  // System.out.println(dataContent);
		    
			mainContent=content1+dataContent+content8;
          //  File file = new File("G:\\workspace\\eventsense\\WebContent\\scripts\\"+jsonFileName+".json");
	
			ServletContext context = request.getServletContext();
			String path = context.getRealPath("/");
		//	System.out.println(path);
			File file = new File(path+"scripts\\"+jsonFileName+".json");
		    // if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(mainContent);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
}	
	

	public String execute()
	{
		ArrayList<String> subSubArray;
		 ArrayList<ArrayList<String>> subArray;
		 entities=myparam.split(",");
         JSONParser parser = new JSONParser();
         try{
        	 for(int entityCounter=1;entityCounter<entities.length;entityCounter++ ){
        		 subArray = new ArrayList<ArrayList<String>>();
        		 jsonFileName=jsonFileName+entities[entityCounter];
        	 String outputString=readUrl("http://dbpedia.org/data/"+entities[entityCounter]+".json");
        	 Object ob;
        	 JSONObject job1;
        	 JSONObject job2;
        	 JSONArray jarray;
        	 Object obj = parser.parse(outputString);    
             JSONObject jsonObject = (JSONObject) obj;
        	 for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
        		    String key = (String) iterator.next();
        		   
        	        ob= jsonObject.get(key);
        		    job1 = (JSONObject) ob;
        		    int indexOfmainArrayList=0;
        		    for(Iterator iterator1 = job1.keySet().iterator(); iterator1.hasNext();){
        		    	 String key1 = (String) iterator1.next();
        		         if(job1.get(key1) instanceof JSONArray)
        		    		 {
        		        	     String mainValue=null;
        		    			 jarray= (JSONArray)job1.get(key1);
        		    			 String mainKey;
        		    			 String demoMainValue;
                                String[] splitKey1=(String[])key1.split("/");
     		    			    mainKey=splitKey1[splitKey1.length-1].toString();
        		    			for( int i=0;i<jarray.size(); i++){
        		    			   
        		    			    job2=(JSONObject)(jarray.get(i));
        		    			     if(mainKey.equals("abstract")){
        		    					if(job2.get("lang").equals("en")){
        		    						   mainValue=job2.get("value").toString();	
  
        		    					}
        		    				}
        		    				else
        		    				{
        		    		            if(job2.get("type").equals("uri"))
        		    		            {
        		    		                if(mainKey.equals("thumbnail"))
        		    		                {
        		    		                	mainValue=job2.get("value").toString();
        		    		                	
        		    		                	
        		    		                }
        		    		              
        		    		                
        		    		                else if(i==0)
        		    		            	{ if(mainKey.equals("knownFor"))
        		    		                	mainValue=key;
        		    		            	  else
        		    		            		mainValue=job2.get("value").toString();
            		    		            	String[] splitMainValue=(String[])mainValue.split("/");
            	        		    			 mainValue=splitMainValue[splitMainValue.length-1].toString();
            	        		    			 mainValue=mainValue.replaceAll("_"," ");
            	        		    			
        		    		            	}
        		    		            	else if(i>0 && i<jarray.size()-1){
        		    		            		if(mainKey.equals("knownFor"))
            		    		                	demoMainValue=key;
        		    		            		else
        		    		            		demoMainValue=job2.get("value").toString();
            		    		            	String[] splitDemoMainValue=(String[])demoMainValue.split("/");
            	        		    			 demoMainValue=splitDemoMainValue[splitDemoMainValue.length-1].toString();
            	        		    			 demoMainValue=demoMainValue.replaceAll("_"," ");	 
            	        		    			 mainValue=mainValue+", "+demoMainValue;
        		    		            	}
        		    		            	else
        		    		            	{
        		    		            		demoMainValue=job2.get("value").toString();
            		    		            	String[] splitDemoMainValue=(String[])demoMainValue.split("/");
            	        		    			 demoMainValue=splitDemoMainValue[splitDemoMainValue.length-1].toString();
            	        		    			 demoMainValue=demoMainValue.replaceAll("_"," ");
            	        		    			 mainValue=mainValue+" and "+demoMainValue;
        		    		            	}
        	        		    			 
        		    		            }
        		    		            else
        		    		            {// if(job2.get("lang").equals("en")){
        		    		            	if(i==0)
        		    		            	{
        		    		            		mainValue=job2.get("value").toString();
        		    		            		mainValue=mainValue.replaceAll("_"," ");
        		    		            	}
        		    		            	else if(i>0 && i<jarray.size()-1){
        		    		            		demoMainValue=job2.get("value").toString();
        		    		            		 demoMainValue=demoMainValue.replaceAll("_"," ");
            		    		            	mainValue=mainValue+", "+demoMainValue;
        		    		            	}
        		    		            	else
        		    		            	{
        		    		            		demoMainValue=job2.get("value").toString();
        		    		            		 demoMainValue=demoMainValue.replaceAll("_"," ");
            		    		                mainValue=mainValue+" and "+demoMainValue;
        		    		            	}
        		    		            //}
        		    		            }
        		    				}
        		    				if(mainValue==null)
        		    					continue;
        		    			
        		    			    }
        		    			subSubArray = new ArrayList<String>();
        		    			
                                subSubArray.add(mainKey);
                                subSubArray.add(mainValue);
                                subArray.add(subSubArray);
        		    			
        		    		 }
        		  
        		    }
        		    
        		}
               mainArrayList.add(subArray);
        	 }
        	 reArrangeMainArrayList(mainArrayList);
       // 	 System.out.println(mainArrayList);
       jsonWriter(mainArrayList);
        	 
         }
         catch (FileNotFoundException e) {
     		e.printStackTrace();
     	} catch (IOException e) {
     		e.printStackTrace();
     	} catch (ParseException e) {
     		e.printStackTrace();
     	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         

         
	    return "test";
	}
	public String getMyparam() {
		return myparam;
	}
	public void setMyparam(String myparam) {
		this.myparam = myparam;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		 this.request = request; 
		
	}
	


	
}
