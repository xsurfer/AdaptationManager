<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
    <link href="${pageContext.request.contextPath}/assets/css/per_page/tuning.css" type="text/css" media="screen" rel="stylesheet" />
    
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">
    
    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->
        
    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div> 
    
  	<div id="col-text">
        
        <h2>Tuning</h2>        
	   	<!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->
	   	
		<form>
		  <fieldset class="resource">		  		 
		  	<legend>Scale:</legend>
		  	<div class="current">	
			  	<ol>
	              <li>
	                <label for="puts_per_tx">Current configuration:</label>
	                <input readonly="readonly" type="text" name="puts_per_tx" id="puts_per_tx" size="2" value="4" />
	              </li>
	              <li>
	                <label for="wr_tx_perc">Current optimal configuration:</label>
	                <input readonly="readonly" type="text" name="wr_tx_perc" id="wr_tx_perc" size="2" value="5" />
	              </li>             
	          </ol>	 
          </div>
               
          <div class="tuning">
          	<ol>
          		<li>
          			<div class="radio_tuning">
          				<input type="radio" name="rd_tx_exec_time_forecast" value="fixed" > Manual tuning
          			</div> 
          			<div class="conf_tuning">
	          			<fieldset>
		          			<input type="text" name="wr_tx_perc" id="wr_tx_perc" size="3" />
		          			<select>
			                  <option value="analytical">Small</option>
			                  <option value="simulator">Medium</option>
			                  <option value="ml">Large</option>
			                </select>
			                <input type="button" value="Set" size="5" />
			            </fieldset>
          			</div>         			
	          		
		            <div class="clc"></div>
	          	</li>
	          	<li>
	          		<div class="radio_tuning">
	          			<input type="radio" name="rd_tx_exec_time_forecast" value="forecast">Self-tuning
	          		</div>	        
	          		<div class="conf_tuning">  		
	          			<fieldset disabled="disabled">
		          			<!-- <label for="wr_tx_local_exec_time">Forecasting method:</label> -->            	                
			                <select>
			                  <option value="analytical">Analytical Method</option>
			                  <option value="simulator">Simulator</option>
			                  <option value="ml">Machine Learning</option>
			                </select> 
		                </fieldset>
	                </div>
	                <div class="clc"></div>	
          		</li>
          	</ol>
          </div> 
          <!-- 
		<div style="float: right">
          	<ol>
          		<li>	          		
	          		<input type="radio" name="rd_tx_exec_time_forecast" value="fixed">Manual tuning	          	          		
          		</li>
          		<li>          				    
	          		<input type="radio" name="rd_tx_exec_time_forecast" value="forecast">Self-tuning	          		         		
	            </li>

          	</ol>
		</div> 
		 -->           
               
      </fieldset>    		 

</form>
	   	
	   	

	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">        
</body>
</html>



