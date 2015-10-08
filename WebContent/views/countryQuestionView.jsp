<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<s:form action="countryquestion">
	<s:select label="select country to see the neighbour country" 
		headerKey="-1" headerValue="Select country"
		list="countryNames" 
		name="submittedCountryName" />
<s:submit value="submit"/>
</s:form>
<s:property value="submittedCountryNames"/>

<s:property value="neighborCountry1" />
<br>
<s:iterator value="neighbor1" var="entry">  
  
<s:property value="#entry.key" /> 
 :
<s:property value="#entry.value" />
</s:iterator>
<br>
<s:property value="neighborCountry2" />
<br>
<s:iterator value="neighbor2" var="entry">  
  
<s:property value="#entry.key" /> 
 :
<s:property value="#entry.value" />
</s:iterator>
<br>
<s:property value="neighborCountry3" />
<br>
<s:iterator value="neighbor3" var="entry">  
  
<s:property value="#entry.key" /> 
 :
<s:property value="#entry.value" />
</s:iterator>


</body>
</html>