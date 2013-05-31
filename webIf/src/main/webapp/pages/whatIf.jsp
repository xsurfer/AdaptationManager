<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
    <link href="${pageContext.request.contextPath}/assets/css/per_page/whatif.css" type="text/css" media="screen" rel="stylesheet" />
    
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">
    
    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->
        
    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div> 
    
  	<div id="col-text">
        
        <h2>What-If Analysis</h2>
        
	   	<!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->
	   				
      	<!--  <a href="${pageContext.request.contextPath}/registration.jsp">Register</a>  -->
		
		
		<form class="whatif">
		  <fieldset>
		  	<legend>Immutable:</legend>
		  	
		  	<ol>
              <li>
                <label for="puts_per_tx">PUTs per TX:</label>
                <input type="text" name="puts_per_tx" id="puts_per_tx" size="3" />
              </li>
              <li>
                <label for="wr_tx_perc">WR TX Percentage:</label>
                <input type="text" name="wr_tx_perc" id="wr_tx_perc" size="3" />
              </li>
              <li>
                <label for="threads">Threads per node:</label>
    		    <input type="text" name="threads" id="threads" size="3" />
              </li>
              <li>
                <label for="acf">ACF:</label>
    		    <input type="text" name="acf" id="acf" size="3" />
              </li>
            </ol>
  		  </fieldset>
  		  
  		  <fieldset>
    		<legend>Mutable:</legend>  
    		<ol>
              <li>
                <label for="wr_tx_local_exec_time">WR TX local exec time:</label>
          		<input type="text" name="wr_tx_local_exec_time" id="wr_tx_local_exec_time" size="3" />
          		<input type="radio" name="wr_tx_local_exec_time_forecast" value="fixed">Fixed
				<input type="radio" name="wr_tx_local_exec_time_forecast" value="forecast">Forecast
              </li>
              <li>
                <label for="rd_tx_exec_time">RD TX exec time:</label>
    		    <input type="text" name="rd_tx_exec_time" id="rd_tx_exec_time" size="3" />
          		<input type="radio" name="rd_tx_exec_time_forecast" value="fixed">Fixed
				<input type="radio" name="rd_tx_exec_time_forecast" value="forecast">Forecast
              </li>
              <li>
                <label for="rtt">RTT:</label>
    		    <input type="text" name="rtt" id="rtt" size="3" />
          		<input type="radio" name="rtt_forecast" value="fixed">Fixed
				<input type="radio" name="rtt_forecast" value="forecast">Forecast
              </li>
            </ol>
  		  </fieldset>
  		  
	      <fieldset>
	        <legend>Action:</legend> 
		      <ol>
	              <li>
	                <label for="wr_tx_local_exec_time">Forecasting method:</label>
	                <select>
	                  <option value="analytical">Analytical Method</option>
	                  <option value="simulator">Simulator</option>
	                  <option value="ml">Machine Learning</option>
	                </select>          		
	              </li>
	              <li></li>
	              <li></li>
	              <li>
	    		    <input type="button" value="Forecast" size="40" />
	              </li>
	              <li>
	                <input type="button" value="Update values from system" size="40" />
	              </li>
	          </ol>	      
	      </fieldset>    		 
    		

		</form>
				

	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">        
</body>
</html>



