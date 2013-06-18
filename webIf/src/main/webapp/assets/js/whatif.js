var REST_GET_VALUES = 'http://' + REST_HOST + ':' + REST_PORT +'/whatif/values';
var REST_GET_WHATIF = 'http://' + REST_HOST + ':' + REST_PORT +'/whatif';

var resources = [ 'abortRate', 'throughput', 'responseTime' ];

var containers = {
		'throughput' : 'placeholderThroughput',
		'abortRate' : 'placeholderAbortRate',
		'responseTime' : 'placeholderResponseTime'
}

var plots = {
		'throughput' : null,
		'abortRate' : null,
		'responseTime' : null
}

var series = {
		"throughput" : [ {
			data : [],
			lines : {
				fill : true,
			},
			curvedLines: {
				apply : true
			}
		} ],
		"abortRate" : [ {
			data : [],
			lines : {
				fill : true
			}
		} ],
		"responseTime" : [ {
			data : [],
			lines : {
				fill : true
			},
			curvedLines: {
				apply : true
			}
		} ]
}

var size = 10;

$(document).ready( 

		function(){

			$('#updateValues').click(function() { 
				retrieveValues(); 
				return false;
			});

			$(":input").focus(function(){
				console.log();
				var label_for = $(this).attr('id');
				$("label[for=" + label_for + "]").css("border-bottom", "1px dashed");
			});

			$(":input").focusout(function(){
				console.log();
				var label_for = $(this).attr('id');
				$("label[for=" + label_for + "]").css("border-bottom", "0px");
			});

			/**
			 * CONTAINER PKRY
			 */
			var container = document.querySelector('#container');

			var pckry = new Packery(container, {
				columnWidth : 430,
				rowHeight : 330
			});

			var itemElems = pckry.getItemElements();
			// for each item...
			for ( var i = 0, len = itemElems.length; i < len; i++) {
				var elem = itemElems[i];
				// make element draggable with Draggabilly
				var draggie = new Draggabilly(elem);
				// bind Draggabilly events to Packery
				pckry.bindDraggabillyEvents(draggie);
			}

			$('#container').bind('dblclick', function(eventObject) {
				$target = $(event.target);
				console.log($target.parent().parent());
				if ($target.hasClass('flot-overlay')) {
					pckry.remove($target.parent().parent());
				}				
			});



			drawPlots();
			getAllAndUpdate();
			//getAllAndUpdate('nodes');
			//getAllAndUpdate('writePercentage');


		}
);

/* *********** */
/* Out of main */
/* *********** */

function getAllAndUpdate() {

	var url = REST_GET_WHATIF;

	$.getJSON(url, function(json) {

		console.log("risorse: " + resources.length);
		for ( var resLen = 0; resLen < resources.length; resLen++) {
			param = resources[resLen];
			console.log("analizzando: " + param);

			dataSize = json[param].length;
			data = json[param];
			console.log("size: " + dataSize);

			var nextData = [];
			for ( var i = 0; i < size; i++)
				nextData[i] = 0;

			for ( var i = 0; i < dataSize; i++) {
				nextData = nextData.slice(1);
				nextData.push(data[i][1]);
			}

			var res = [];
			for ( var i = 0; i < nextData.length; ++i) {
				res.push([ i, nextData[i] ]);
			}

			var currSeries = series[param];
			currSeries[0].data = res;

			var currPlot = plots[param];
			currPlot.setData(currSeries);

			currPlot.setupGrid();
			currPlot.draw();
		}

	});
}


function drawPlots() {
	for ( var i = 0; i < resources.length; i++) {
		element = resources[i];

		var containerId = containers[element];
		var container = $("#" + containerId);
		var currSeries = series[element];

		var plot = $
		.plot(
				container,
				currSeries,
				{
					grid : {
						borderWidth : 1,
						minBorderMargin : 20,
						labelMargin : 10,
						backgroundColor : {
							colors : [ "#fff", "#e4f4f4" ]
						},
						margin : {
							top : 8,
							bottom : 20,
							left : 20
						},
						markings : function(axes) {
							var markings = [];
							var xaxis = axes.xaxis;
							for ( var x = Math.floor(xaxis.min); x < xaxis.max; x += xaxis.tickSize * 2) {
								markings
								.push({
									xaxis : {
										from : x,
										to : x
										+ xaxis.tickSize
									},
									color : "rgba(232, 232, 255, 0.2)"
								});
							}
							return markings;
						}
					},
					xaxis : {
						tickFormatter : function() {
							return "";
						}
					},
					yaxis : {
						min : 0,
						autoscaleMargin : 0.02
						//max : 10
					},
					legend : {
						show : true
					},
					series: {
						curvedLines: {
							active: true
						}
					}
				});
		plots[element] = plot;
	}
}

function retrieveValues(){
	console.log(REST_GET_VALUES);
	$.getJSON( REST_GET_VALUES, function(data){

		$.each(data, function(key, val) {
			console.log("key: " + key);
			console.log("val: " + val);
			$('#' + key ).val(val);
		});



		console.log(data);
		//alert("ricevuto");
		/*
		var items = [];
		$.each(data, function(key, val) {
			var htmlField = resourceToFeature[key];

			if(htmlField){	
				console.log("htmlField: " + htmlField);
				console.log("val:" + val);
				var field = featureToField[htmlField];
				console.log("field:" + field);
				console.log("valore:" + val[field]);
				$('span#' + current + htmlField ).text(val[field]);
				$('span#' + currentOpt + htmlField ).text("OPT");				
			}						
		});*/
		//alert(items);
	});
}


