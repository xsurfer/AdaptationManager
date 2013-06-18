<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="MyCloud.message" /></title>
<link
	href="${pageContext.request.contextPath}/assets/css/per_page/whatif.css"
	type="text/css" media="screen" rel="stylesheet" />
	
<script type="text/javascript">
	var REST_HOST = '<s:property value="getRestHost()" />';
	var REST_PORT = '<s:property value="getRestPort()" />';
</script>
	
	<!-- PACKERY + DRAGGABILLY -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/packery.pkgd.min.js" ></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/draggabilly.js" ></script>
	
	<!-- FLOT + SMOOTH -->
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/flot/jquery.flot.min.js" ></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/flot/curvedLines.js" ></script>    
	
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/whatif.js"></script>
	<script src="http://malsup.github.com/jquery.form.js"></script>
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">

		<div id="ribbon"></div>
		<!-- /ribbon (design/ribbon.gif) -->

		<!-- Screenshot in browser (replace tmp/browser.gif) -->
		<div id="col-browser"></div>

		<div id="col-text">

			<h2>What-If Analysis</h2>

			<!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->

			<!--  <a href="${pageContext.request.contextPath}/registration.jsp">Register</a>  -->


			<form id="whatif">
			
				<div class="table">
					<div class="row">
						<div class="column first">
							<fieldset>
								<legend>Data Access Pattern:</legend>
									<div class="param">
										<label for="acf">Application Contention Factor:</label>
										<input type="text" id="acf" name="acf" size="5" />
									</div>
									<div class="param">
										<label for="RetryWritePercentage">Write Transactions Percentage:</label>
										<input type="text" name="RetryWritePercentage" id="RetryWritePercentage" size="5" />
									</div>
									<div class="param">
										<label for="SuxNumPuts"># PUT per write transaction:</label>
										<input type="text" name="SuxNumPuts" id="SuxNumPuts" size="5" />
									</div>
									<div class="param">
										<label for="GetWriteTx"># GET per write transaction:</label>
										<input type="text" name="GetWriteTx" id="GetWriteTx" size="5" />
									</div>
									<div class="param">
										<label for="GetReadOnlyTx"># GET per read only transaction:</label>
										<input type="text" name="GetReadOnlyTx" id="GetReadOnlyTx" size="5" />
									</div>
									<div class="param">
									</div>							
							</fieldset>											
						</div>
					</div>
					<div class="row">
						<div class="column">
							<fieldset>
								<legend>CPU Costs:</legend>
									<div class="param">
										<label for="LocalUpdateTxLocalServiceTime">Write transaction demand:</label>
										<input type="text" name="LocalUpdateTxLocalServiceTime" id="LocalUpdateTxLocalServiceTime" size="5" />
									</div>
									<div class="param">
										<label for="LocalReadOnlyTxLocalServiceTime">Read Only transaction demand:</label>
										<input type="text" name="LocalReadOnlyTxLocalServiceTime" id="LocalReadOnlyTxLocalServiceTime" size="5" />																	
									</div>
									<div class="param">
									</div>
					
							</fieldset>											
						</div>
					</div>
					<div class="row">
						<div class="column">
							<fieldset>
								<legend>NET Costs:</legend>
									<div class="param">
										<div class="line"></div>
										<label for="PrepareCommandBytes">Size of prepare message:</label>										
										<input type="text" name="PrepareCommandBytes" id="PrepareCommandBytes" size="5" />
									</div>
									<div class="param">
										<label for="RTT">Prepare latency:</label>
										<input type="text" name="RTT" id="RTT" size="5" />
									</div>
									<div class="param">
										<label for="CommitBroadcastWallClockTime">Commit latency:</label>
										<input type="text" name="CommitBroadcastWallClockTime" id="CommitBroadcastWallClockTime" size="5" />
									</div>
									<div class="param">
										<label for="RemoteGetLatency">Remote get latency:</label>
										<input type="text" name="RemoteGetLatency" id="RemoteGetLatency" size="5" />
									</div>
									<div class="param">
									</div>
									<div class="param">
									</div>
								</fieldset>											
						</div>
					</div>
					<div class="action">
								<input id="updateValues" type="submit" class="submit" value="Update values from system" size="40" />
								<input type="button" class="submit" value="Forecast" size="40" /> 																
					</div>
					
				</div>				
			</form>
			
			<div id="container">
				<div class="item">
					<div class="plotTitle">Throughput</div>
					<div id="placeholderThroughput" class="plot"></div>
				</div>
				<div class="item">
					<div class="plotTitle">Nodes</div>
					<div id="placeholderResponseTime" class="plot"></div>
				</div>
				<div class="item">
					<div class="plotTitle">Write %</div>
					<div id="placeholderAbortRate" class="plot"></div>
				</div>
			</div>	   				
			


		</div>
		<!-- /col-text -->

	</div>
	<!-- /col -->
	<div id="col-bottom"></div>

	<hr class="noscreen">
	<hr class="noscreen">
</body>
</html>



