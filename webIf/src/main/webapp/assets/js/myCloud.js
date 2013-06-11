$(function() {

	// We use an inline data source in the example, usually data would
	// be fetched from a server

	var dataNodes = [], dataResponseTime, totalPoints = 300;
	
	
	function getNumNodes(){

		if (dataNodes.length > 0)
			dataNodes = dataNodes.slice(1);

		// Do a random walk

		while (dataNodes.length < totalPoints) {

			var prev = dataNodes.length > 0 ? dataNodes[dataNodes.length - 1] : 50, y = prev
					+ Math.random() * 10 - 5;

			if (y < 0) {
				y = 0;
			} else if (y > 100) {
				y = 100;
			}

			dataNodes.push(y);
		}

		// Zip the generated y values with the x values

		var res = [];
		for ( var i = 0; i < dataNodes.length; ++i) {
			res.push([ i, dataNodes[i] ])
		}

		return res;	
	}	
	

	function getResponseTime() {
		if (dataResponseTime.length > 0)
			dataResponseTime = dataResponseTime.slice(1);

		// Do a random walk

		while (dataResponseTime.length < totalPoints) {

			var prev = dataResponseTime.length > 0 ? dataResponseTime[dataResponseTime.length - 1] : 50, y = prev
					+ Math.random() * 10 - 5;

			if (y < 0) {
				y = 0;
			} else if (y > 100) {
				y = 100;
			}

			dataResponseTime.push(y);
		}

		// Zip the generated y values with the x values

		var res = [];
		for ( var i = 0; i < dataResponseTime.length; ++i) {
			res.push([ i, dataResponseTime[i] ])
		}

		return res;
	}

	// Set up the control widget

	var updateInterval = 300;
	$("#updateInterval").val(updateInterval).change(function() {
		var v = $(this).val();
		if (v && !isNaN(+v)) {
			updateInterval = +v;
			if (updateInterval < 1) {
				updateInterval = 1;
			} else if (updateInterval > 2000) {
				updateInterval = 2000;
			}
			$(this).val("" + updateInterval);
		}
	});

	seriesNodes = [ {
		data : getNumNodes(),
		lines : {
			fill : true
		}
	} ];

	var container = $("#placeholderNumNodes");
	
	var plot = $
			.plot(
					container,
					seriesNodes,
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
									markings.push({
										xaxis : {
											from : x,
											to : x + xaxis.tickSize
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
							max : 110
						},
						legend : {
							show : true
						}
					});

	setInterval(function updateRandom() {
		seriesNodes[0].data = getNumNodes();
		plot.setData(seriesNodes);
		plot.draw();
	}, 40);

});
