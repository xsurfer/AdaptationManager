<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
    
    <style type="text/css">
    	p {
    		display: inline;
    	}
    </style>
    
<script type="text/javascript">
	var REST_HOST = '<s:property value="getRestHost()" />';
	var REST_PORT = '<s:property value="getRestPort()" />';
</script>
    
<script type="text/javascript">

var REST_STATUS = 'http://' + REST_HOST + ':' + REST_PORT +'/status';

$(document).ready(
		function(){
			Retrieve();
		}    		
);
    
    var Polling = function(){
    	setTimeout(Retrieve, 5000);
    }
    
    var Retrieve = function(){
    	
    	$.getJSON( REST_STATUS, function(data){Update(data)})
    		.complete(function(){
    			Polling();
    		})
    		.fail(function() { 
    			setTimeout(function(){
            		document.location.reload(true);	
            	}, 5000);
    		});
    	/*
    	$.ajax({
            type: 'GET',
            crossDomain:true,
            dataType: 'json',
            url: 'http://46.252.152.83:9998/status',
            success: function (data) {
            	Update(data);
            },
            error: function (request, status, error) {
//            	setTimeout(function(){
//            		document.location.reload(true);	
//            	}, 5000);
		console.log(status);
		console.log(error);            	
            },
            complete: Polling
        });  
    	*/
    }
    
/**
{
   "status":"WORKING",
   "scale":{
      "small":0,
      "medium":0,
      "large":0,
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "replicationProtocol":{
      "protocol":"TWOPC",
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "replicationDegree":{
      "degree":2,
      "type":"AUTO",
      "method":"ANALYTICAL"
   },
   "dataPlacement":{
      "type":"AUTO",
      "method":"ANALYTICAL"
   }
}
*/
    
    var Update = function(json) {
    	console.log(json); 
		
		$("span#status").text(json.platformState);
		
		
		/* SCALE */
		$("p#scale_tuning").text($.trim(json.scale.tuning) + " + " + $.trim(json.scale.forecaster));
		
		var instanceType = (json.scale.instanceType == "NONE") ? "" : json.scale.instanceType;
		
		$("p#scale_conf").text( $.trim(json.scale.size) + " " + $.trim(instanceType));
		
		$("p#rep_degree_tuning").text( $.trim(json.replicationDegree.tuning) + " + " + $.trim(json.replicationDegree.forecaster) );
		$("p#rep_degree_conf").text( $.trim(json.replicationDegree.degree) );
		
		$("p#rep_prot_tuning").text( $.trim(json.replicationProtocol.tuning) + " + " + $.trim(json.replicationProtocol.forecaster) );
		$("p#rep_prot_conf").text( $.trim(json.replicationProtocol.protocol) );
		
		$("p#placement_tuning").text( $.trim(json.dataPlacement.tuning) + " + " + $.trim(json.dataPlacement.forecaster) );
		
	};

    </script>
    
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
        <h3>Status: <span style="display: inline;" id="status"></span></h3>
        
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
		            <td><p id="scale_tuning"></p></td>
		            <td><p id="scale_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Replication Degree</td>
		            <td><p id="rep_degree_tuning"></p></td>
		            <td><p id="rep_degree_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Protocol Switching</td>
		            <td><p id="rep_prot_tuning"></p></td>
		            <td><p id="rep_prot_conf"></p></td>
		            <td>OPT</td>
		        </tr>
		        <tr>
		        	<td>Data Placement</td>
		            <td><p id="placement_tuning"></p></td>
		            <td>--</td>
		            <td>--</td>
		        </tr>
		    </tbody>
		</table>
			   				
	   				
	   				
	   	<!-- 			
      	<a href="${pageContext.request.contextPath}/registration.jsp">Register</a>
		 -->

	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">        
</body>
</html>



