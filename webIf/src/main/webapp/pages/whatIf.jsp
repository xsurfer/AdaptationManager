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
			
			
			<!--
				<div class="table">
					<div class="row">
						<div class="column">
							<div class="table">
								<div class="row">
									
									<div class="column immutable">
										<fieldset>
											<legend>Immutable:</legend>
											<ol>
												<li><label for="puts_per_tx">PUTs per TX:</label> <input
													type="text" name="puts_per_tx" id="puts_per_tx" size="3" />
												</li>
												<li><label for="wr_tx_perc">WR TX Percentage:</label> <input
													type="text" name="wr_tx_perc" id="wr_tx_perc" size="3" />
												</li>
												<li><label for="threads">Threads per node:</label> <input
													type="text" name="threads" id="threads" size="3" /></li>
												<li><label for="acf">ACF:</label> <input type="text"
													name="acf" id="acf" size="3" /></li>
											</ol>
										</fieldset>
									</div>


									<div class="column mutable">
										<fieldset>
											<legend>Mutable:</legend>
											<ol>
												<li><label for="wr_tx_local_exec_time">WR TX
														local exec time:</label> <input type="text"
													name="wr_tx_local_exec_time" id="wr_tx_local_exec_time"
													size="3" /> <input type="radio"
													name="wr_tx_local_exec_time_forecast" value="fixed">Fixed
													<input type="radio" name="wr_tx_local_exec_time_forecast"
													value="forecast">Forecast</li>
												<li><label for="rd_tx_exec_time">RD TX exec
														time:</label> <input type="text" name="rd_tx_exec_time"
													id="rd_tx_exec_time" size="3" /> <input type="radio"
													name="rd_tx_exec_time_forecast" value="fixed">Fixed
													<input type="radio" name="rd_tx_exec_time_forecast"
													value="forecast">Forecast</li>
												<li><label for="rtt">RTT:</label> <input type="text"
													name="rtt" id="rtt" size="3" /> <input type="radio"
													name="rtt_forecast" value="fixed">Fixed <input
													type="radio" name="rtt_forecast" value="forecast">Forecast
												</li>
											</ol>
										</fieldset>
									</div>

									<div class="column mutable">
										<fieldset>
											<legend>Forecasters:</legend>
											<ol>
												<li><input type="checkbox" name="vehicle" value="Bike">Analytical</li>
												<li><input type="checkbox" name="vehicle" value="Car">Simulator</li>
												<li><input type="checkbox" name="vehicle" value="Car">Machine
													Learner</li>
											</ol>
										</fieldset>
									</div>

								</div>
							</div>

						</div>
					</div>
					<div class="row">
						<div class="action">
							<ol>
								<li><input type="button" value="Forecast" size="40" /> <input
									type="button" value="Update values from system" size="40" /></li>

							</ol>
						</div>
					</div>
				</div> -->
			</form>


		</div>
		<!-- /col-text -->

	</div>
	<!-- /col -->
	<div id="col-bottom"></div>

	<hr class="noscreen">
	<hr class="noscreen">
</body>
</html>



