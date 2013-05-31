<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">
    
    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->
        
    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div> 
    
  	<div id="col-text">
        
        <h2><s:property value="message"/></h2>
        <!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->
	   				
	   				
	   				
		<table id="box-table-a" summary="Employee Pay Sheet">
		    <thead>
		    	<tr>
		        	<th scope="col">Autotuned Feature</th>
		            <th scope="col">Status</th>
		            <th scope="col">Current</th>
		            <th scope="col">Optimal</th>
		        </tr>
		    </thead>
		    <tbody>
		    	<tr>
		        	<td>Scale</td>
		            <td><s:property value="scale.printTuning()"/></td>
		            <td><s:property value="scale.small"/> S /<s:property value="scale.medium"/> M / <s:property value="scale.large"/> L</td>
		            <td>Conf</td>
		        </tr>
		        <tr>
		        	<td>Replication Degree</td>
		            <td>status</td>
		            <td>20</td>
		            <td>30</td>
		        </tr>
		        <tr>
		        	<td>Protocol Switching</td>
		            <td>status</td>
		            <td>2PC</td>
		            <td>TO</td>
		        </tr>
		        <tr>
		        	<td>Data Placement</td>
		            <td>status</td>
		            <td>--</td>
		            <td>--</td>
		        </tr>
		    </tbody>
		</table>
			   				
	   				
	   				
	   				
      	<a href="${pageContext.request.contextPath}/registration.jsp">Register</a>
		

	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">        
</body>
</html>



