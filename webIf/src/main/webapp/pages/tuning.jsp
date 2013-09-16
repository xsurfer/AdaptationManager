<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title><s:text name="MyCloud.message" /></title>
<link
	href="${pageContext.request.contextPath}/assets/css/per_page/tuning.css"
	type="text/css" media="screen" rel="stylesheet" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fancybox/jquery.fancybox.css" type="text/css" media="screen" />
<script type="text/javascript">
	var REST_HOST = '<s:property value="getRestHost()" />';
	var REST_PORT = '<s:property value="getRestPort()" />';
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/tuning.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/fancybox/jquery.fancybox.pack.js"></script>
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

			<h2>Tuning</h2>
			<!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->

			<!-- FORECASTER FORM -->
			

				<div class="resource">
					<fieldset class="resource">
						<legend>Forecaster:</legend>
						
						<!-- CURRENT -->
						<div class="current">
							<ol>
								<li>
									<p class="label">Current configuration:</p>
									<p class="config"><span id="current_forecaster">N/A</span></p>								
								</li>
							</ol>
						</div>		
						
						<!-- UPDATE -->
						<div class="update">	
							<form id="updateAll">
								<input class="submit update" type="button" value="Update all forecasts" />
							</form>						
						</div>				
						
						<!-- TUNING -->
						<form id="forecaster" action="">
						<div class="tuning">
							<ol>							
								<li>
									<div class="">
										<fieldset name="forecaster_tuning_self">
											<!-- <label for="wr_tx_local_exec_time">Forecasting method:</label> -->
											<select name="forecaster" class="forecasting">
												<option selected value="ANALYTICAL">Analytical</option>
												<option value="SIMULATOR">Simulator</option>
												<option value="MACHINE_LEARNING">Machine Learning</option>
												<option value="COMMITTEE">Committee</option>
											</select>
										</fieldset>										
									</div>
								</li>
							</ol>
						</div>
						
						<!-- CONTROL -->
						<div class="control">
							<input class="submit" type="submit" value="Set" />
						</div>
						</form>
					</fieldset>
				</div>
			
				
				
			<!-- SCALE FORM -->
			
				<!-- RESOURCE -->
				<div class="resource">
					<fieldset class="resource">
						<legend>Scale:</legend>
						
						<!-- CURRENT -->
						<div class="current">
							<ol>
								<li>
									<p class="label">Current configuration:</p>
									<p class="config"><span id="current_scale">N/A</span>instances</p>								
								</li>
								<li>
									<p class="label">Optimal configuration:</p>
									<p class="config"><span id="current_opt_scale">N/A</span>instances</p>								
								</li>
							</ol>
						</div>	
						
						<!-- UPDATE -->
						<div class="update">
						<form id="updateScale" action="#">
							<input name="xaxis" type="hidden" value="NODES" />						
							<input name="fixed_degree" type="hidden" value="2" />
							<input name="fixed_protocol" type="hidden" value="TWOPC" />
						
							<button class="submit update"> 
								Update Prediction<br /> Optimal Scale
							</button>		
						</form>
						</div>					
						
						<!-- TUNING -->
						<form id="scale">
						<div class="tuning">							
							<ol>
								<li>
									<div class="radio_tuning">
										<input type="radio" name="scale_tuning" value="FALSE" />
										Manual tuning
									</div>
									<div class="conf_tuning">
										<fieldset name="scale_tuning_manual">
											<input type="text" name="scale_size" id=scale_size size="3" />
<%-- 											<select name="instance_type"> --%>
<!-- 												<option value="SMALL">Small</option> -->
<!-- 												<option selected value="MEDIUM">Medium</option> -->
<!-- 												<option value="LARGE">Large</option> -->
<%-- 											</select> --%>
										</fieldset>
									</div>		

								</li>
								<li>
									<div class="radio_tuning">
										<input type="radio" name="scale_tuning" value="TRUE" />Self-tuning
									</div>																	
								</li>
							</ol>
						</div>
						
						<!-- CONTROL -->
						<div class="control">
							<input class="submit" type="submit" value="Set" />
						</div>
						</form>
					</fieldset>
				</div>			

			<!-- REPLICATION DEGREE FORM -->
			
			
				<!-- RESOURCE -->
				<div class="resource">
					<fieldset class="resource">
						<legend>Replication Degree:</legend>
						
						<!-- CURRENT -->
						<div class="current">
							<ol>
								<li>
									<p class="label">Current configuration:</p>
									<p class="config"><span id="current_rep_degree">N/A</span>replicas per object</p>								
								</li>
								<li>
									<p class="label">Optimal configuration:</p>
									<p class="config"><span id="current_opt_rep_degree">N/A</span>replicas per object</p>
								</li>
							</ol>
						</div>		
						
						<!-- UPDATE -->
						<div class="update">	
							<form>
								<button class="submit update"> 
									Update Prediction<br /> Optimal Rep. Degree
								</button>
							</form>						
						</div>							
						
						<!-- TUNING -->
						<form id="degree" action="" >
						<div class="tuning">
							<ol>
								<li>
									<div class="radio_tuning">
										<input type="radio" name="rep_degree_tuning" value="FALSE" />
										Manual tuning
									</div>
									<div class="conf_tuning">
										<fieldset name="rep_degree_tuning_manual">
											<input type="text" name="rep_degree_size" id=degree_conf size="3" />											
										</fieldset>
									</div>	
									

								</li>
								<li>
									<div class="radio_tuning">
										<input type="radio" name="rep_degree_tuning" value="TRUE" />Self-tuning
									</div>
								</li>
							</ol>
						</div>
						
						<!-- CONTROL -->
						<div class="control">
							<input class="submit" type="submit" value="Set" />
						</div>
						</form>
					</fieldset>
				</div>
			

			<!-- PROTOCOL SWITCHING FORM -->
			
			
				<!-- RESOURCE -->
				<div class="resource">
					<fieldset class="resource">
						<legend>Protocol Switching:</legend>
						
						<!-- CURRENT -->
						<div class="current">
							<ol>
								<li>
									<p class="label">Current configuration:</p>
									<p class="config"><span id="current_rep_protocol">N/A</span></p>								
								</li>
								<li>
									<p class="label">Optimal configuration:</p>
									<p class="config"><span id="current_opt_rep_protocol">N/A</span></p>
								</li>
							</ol>
						</div>
						
						<!-- UPDATE -->
						<div class="update">							
							<form>
								<button class="submit update"> 
									Update Prediction<br /> Optimal Rep. Scheme
								</button>								
							</form>						
						</div>												
						
						<!-- TUNING -->
						<form id="protocol" action="#" >
						<div class="tuning">
							<ol>
								<li>
									<div class="radio_tuning">										
										<input type="radio" id="rep_protocol_tuning_manual" name="rep_protocol_tuning" value="FALSE" />
										<label for="rep_protocol_tuning_manual">Manual tuning</label>										
									</div>
									<div class="conf_tuning">
										<fieldset name="rep_protocol_tuning_manual">
											<select name="rep_protocol">
												<option selected value="2PC">2PC</option>
												<option value="TO">TO</option>
												<option value="PB">PB</option>
											</select>											
										</fieldset>
									</div>	
									

								</li>
								<li>
									<div class="radio_tuning">
										<input type="radio" id="rep_protocol_tuning_self" name="rep_protocol_tuning"  value="TRUE" />
										<label for="rep_protocol_tuning_self">Self-tuning</label>
									</div>
								</li>
							</ol>
						</div>
						
						<!-- CONTROL -->
						<div class="control">
							<input class="submit" type="submit" value="Set" />
						</div>
						</form>
					</fieldset>
				</div>
			

			<!-- DATA PLACEMENT FORM -->			
			
				<!-- RESOURCE -->
				<div class="resource">
					<fieldset class="resource">
						<legend>Auto-Placer:</legend>
												
						<!-- CURRENT -->
						<div class="current autoplacer">
							<ol>
								<li>
									<p class="label autoplacer">AVG Remote OPS/ReadOnly TX:</p>
									<p class="config"><span id="current_data_placement">N/A</span></p>								
								</li>
								<li>
									<p class="label autoplacer">AVG Remote OPS/Update TX:</p>
									<p class="config"><span id="current_data_placement">N/A</span></p>								
								</li>
								<li>
									<p class="label autoplacer">AVG Nodes involved in TX:</p>
									<p class="config"><span id="current_opt_data_placement">N/A</span></p>
								</li>
							</ol>
						</div>	
						
<!-- 						UPDATE -->
<!-- 						<div class="update">													 -->
<!-- 						</div>						 -->
						
						<!-- TUNING -->
						<form id="autoplacer" action="#">
						<div class="tuning autoplacer">
							<ol>
								<li>
									<div class="autoplacer_tuning">										
										<input type="radio" id="autoplacer_tuning_manual" name="autoplacer_tuning" value="TRUE" />
										<label for="autoplacer_tuning_manual">Perform Single Optimization Round</label>										
									</div>
								
								
								

<!-- 									<div class="conf_tuning"> -->
<!-- 										<input type="checkbox" name="data_placement_tuning" value="Bike" />Self-tuning										 -->
<!-- 									</div>									 -->
								</li>
								<li>
									<div class="autoplacer_tuning">
										<input type="radio" id="autoplacer_tuning_self" name="autoplacer_tuning" value="FALSE" />
										<label for="autoplacer_tuning_self">Trigger automatically when:</label>
										
									</div>
									<div>
										<fieldset class="autoplacer" name="autoplacer_tuning_manual">
											<div class="radio_tuning autoplacer">
											<ol>
												<li>
													<input id="remote_ops_ro_tx" type="checkbox" />
													<label for="remote_ops_ro_tx">Remote Ops (RO TX) ></label>
													<input type="text" name="rep_degree_size" id=degree_conf size="3" />																						
												</li>
												<li>
													<input id="remote_ops_up_tx" type="checkbox" />
													<label for="remote_ops_up_tx" >Remote Ops (UP TX) ></label>
													<input type="text" name="rep_degree_size" id=asd size="3" />																						
												</li>
												<li>
													<input id="avg_nodes_in_tx" type="checkbox" />
													<label for="avg_nodes_in_tx">AVG #Nodes in TX ></label>
													<input type="text" name="rep_degree_size" id=asd size="3" />																						
												</li>
												
											</ol>
											</div>
											<div class="conf_tuning autoplacer">
											<ol>
												<li>
													<label for="time_bt_rounds">Time b/t Rounds (mins):</label>
													<input id="time_bt_rounds" type="text" name="rep_degree_size" id=asd size="3" />
												</li>
												<li>
													<label for="keys_shifted_per_round">Keys shifted per Round (per Node):</label>
													<input id="keys_shifted_per_round" type="text" name="rep_degree_size" id=asd size="3" />
												</li>
												<li>
													<label for="max_rounds">Max #Rounds:</label>
													<input id="max_rounds" type="text" name="rep_degree_size" id=asd size="3" />
												</li>
											</ol>
											</div>
																						
										</fieldset>
									</div>
								</li>
							</ol>
						</div>
						
						<!-- CONTROL -->
						<div class="control">
							<input class="submit" type="submit" value="Set" />
						</div>
						</form>
					</fieldset>
				</div>
			

		</div>
		<!-- /col-text -->

	</div>
	<!-- /col -->
	<div id="col-bottom"></div>

	<hr class="noscreen" />
	<hr class="noscreen" />
</body>
</html>



