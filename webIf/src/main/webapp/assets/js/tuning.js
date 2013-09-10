var resources = [
                 "scale",
                 "rep_degree",
                 "rep_protocol",
                 "autoplacer"
                 ];

var resourceToFeature = { "scale":"scale", 
						"replicationDegree":"rep_degree", 
						"replicationProtocol":"rep_protocol",
						"dataPlacement":"data_placement"
					  };
var featureToField = { 
		"scale":"size", 
		"rep_protocol":"protocol", 
		"rep_degree":"degree",
		//"dataPlacement":"data_placement"
	  };

var REST_STATUS = 'http://' + REST_HOST + ':' + REST_PORT +'/status';
var REST_SET_SCALE = 'http://' + REST_HOST + ':' + REST_PORT +'/tuning/scale';
var REST_SET_DEGREE = 'http://' + REST_HOST + ':' + REST_PORT +'/tuning/degree';
var REST_SET_PROTOCOL = 'http://' + REST_HOST + ':' + REST_PORT +'/tuning/protocol';

var REST_SET_FORECASTER = 'http://' + REST_HOST + ':' + REST_PORT +'/tuning/forecaster';

var REST_UPDATE_ALL = 'http://' + REST_HOST + ':' + REST_PORT +'/tuning/updateAll';

$(document).ready( 
    		
    		function(){
    			console.log("REST_STATUS: " + REST_STATUS);
    			init(); 
    			$('.fancybox').fancybox();

    			$('#forecaster').submit(function() {
    				alert("forecaster");
                    sendForecaster(); 
                    return false;
                });
    			
    			$('#scale').submit(function() { 
                    sendScale(); 
                    return false;
                });
    			
    			$('#degree').submit(function() { 
                    sendDegree(); 
                    return false;
                });
    			
    			$('#protocol').submit(function() { 
                    sendProtocol(); 
                    return false;
                });
    			
    			$('#autoplacer').submit(function() {                     
                    return false;
                });
    			
    			
    			$('#updateAll').click(function() { 
                    updateAll(); 
                    return false;
                });


    			
    			
    		}    
    		    		
);

function init(){
	init_radioBtn();
	initCurrentConfig();
}

/*
{
	"currentState":"RUNNING",
	"tuning":{
		"forecaster":"ANALYTICAL",
		"autoScale":true,
		"autoDegree":true,
		"autoProtocol":true
	}, 
	"configuration":{
		"platformSize":2,"threadPerNode":3,
		"nodesConfig":"MEDIUM",
		"replicationProtocol":"TWOPC",
		"replicationDegree":2,
		"dataPlacement":false
	}
}
*/

function initCurrentConfig(){
	var current = 'current_';
	var currentOpt = 'current_opt_';
	
	$.getJSON( REST_STATUS, function(json){
		console.log(json);
		
		$('span#current_forecaster').text( json.tuning.forecaster );
		$('span#current_scale').text( json.configuration.platformSize );
		$('span#current_rep_degree').text( json.configuration.replicationDegree );
		$('span#current_rep_protocol').text( json.configuration.replicationProtocol );
		
	});
	
	/*
	 * {"platformSize":10,"threadPerNode":2,"nodesConfig":"MEDIUM","replicationProtocol":"PB","replicationDegree":10,"dataPlacement":{"value":0}}
	 */
	
	$.getJSON( REST_UPDATE_ALL, function(json){
		console.log(json);
		
		$('span#current_opt_scale').text( json.platformSize );
		$('span#current_opt_rep_degree').text( json.replicationDegree );
		$('span#current_opt_rep_protocol').text( json.replicationProtocol );
		
				
		});

	
}

function init_radioBtn(){	
	
	var element = null;
	
	for (var i = 0; i < resources.length; i++) {		
		console.log(resources.length);
		element = resources[i];
		
		
		var $radios = $('input:radio[name='+element+'_tuning]');
				
		// INIT
		if(element != 'autoplacer'){		
			if($radios.is(':checked') === false) {
				$radios.filter('[value=FALSE]').prop('checked', true);
				disableFieldset($radios);
			}
		} else {
			if($radios.is(':checked') === false) {
				$radios.filter('[value=TRUE]').prop('checked', true);
				disableFieldset($radios);
			}
			
		}
	    
	    // LISTENER
	    $radios.change(function(){
	    	disableFieldset($(this));
	    });	      
	}	
}

function disableFieldset($radio){

	
	console.log("radio val: " + $radio.val()); // TRUE o FALSE	
	console.log("radio name: " +$radio.attr("name"));
	// seleziono tutte le fieldset che non si chiamano in quel modo e le disattivo
	
	// name + '_' + value
	
	var fieldsetName = $radio.attr("name") + '_';
	var enabled = disabled = fieldsetName;
	if($radio.attr("value")=="TRUE"){
		//enabled += 'self';
		disabled += 'manual';
		console.log("disabled fieldset: " + disabled);
		$('fieldset[name="'+ disabled +'"]').attr("disabled", "disabled");
	} else {
		enabled += 'manual';
		console.log("enabled fieldset: " + enabled);
		$('fieldset[name="'+ enabled +'"]').removeAttr("disabled");
		//disabled += 'self';
	}			
}

function sendForecaster() {
	var dataToBeSent = $("form#forecaster").serialize();
	console.log("forecaster - dataToBeSent: " + dataToBeSent);
	
	//$('#form#forecaster').ajaxForm();
	
	$.ajax({
		dataType: "json",
		contentType: "application/x-www-form-urlencoded",		
		url: REST_SET_FORECASTER,
		type: "POST",
		crossDomain: true,
		data: dataToBeSent,	

		success: function(data) {	        
	        alert('ok!');
	    },
		error: function(xhr, status) {
			console.log(xhr);
			alert(status);
		}
	});
	
	
//	$.ajax({
//	    url: REST_SET_FORECASTER,
//	    type: 'PUT',
//	    crossDomain: true,
//	    data: dataToBeSent,
//	    dataType: 'json',
//	    success: function(data) {	        
//	        alert('ok!');
//	    }
//	});
}

function sendScale() {
	var dataToBeSent = $("form#scale").serialize();
	$('#form#scale').ajaxForm();
	
	$.ajax({
		dataType: "json",
		contentType: "application/x-www-form-urlencoded",		
		url: REST_SET_SCALE,
		type: "POST",
		crossDomain: true,
		data: dataToBeSent,	

		success: function(data) {	        
	        alert('ok! ' + data);
	    },
		error: function(xhr, status) {
			console.log(xhr);
			alert(status);
		}
	});	
}

function updateAll() {
	
	$.ajax({
		dataType: "json",
		contentType: "application/x-www-form-urlencoded",		
		url: REST_UPDATE_ALL,
		type: "GET",
		crossDomain: true,

		success: function(data) {	        
	        console.log(data);
	    },
		error: function(xhr, status) {
			console.log(xhr);
			alert(status);
		}
	});
}

function sendDegree() {
	var dataToBeSent = $("form#degree").serialize();
	$('#form#degree').ajaxForm();
	
	$.ajax({
		dataType: "json",
		contentType: "application/x-www-form-urlencoded",		
		url: REST_SET_DEGREE,
		type: "POST",
		crossDomain: true,
		data: dataToBeSent,	

		success: function(data) {	        
	        alert('ok! ' + data);
	    },
		error: function(xhr, status) {
			console.log(xhr);
			alert(status);
		}
	});
	
}


function sendProtocol() {
	var dataToBeSent = $("form#protocol").serialize();
	$('#form#protocol').ajaxForm();
	
	$.ajax({
		dataType: "json",
		contentType: "application/x-www-form-urlencoded",		
		url: REST_SET_PROTOCOL,
		type: "POST",
		crossDomain: true,
		data: dataToBeSent,	

		success: function(data) {	        
	        alert('ok! ' + data);
	    },
		error: function(xhr, status) {
			console.log(xhr);
			alert(status);
		}
	});
	
}

//$.post(REST_SET_SCALE, dataToBeSent);
/*
console.log("DATA_TO_SENT" + dataToBeSent);
$.ajax({
    type: 'PUT',
    crossDomain: true,
    data: {}
    //dataType: 'json',
    url: REST_SET_SCALE,
    success: function (data) {
    	alert("Thank you for your comment!");
    },
    error: function (request, status, error) {
//    	setTimeout(function(){
//    		document.location.reload(true);	
//    	}, 5000);
    console.log(status);
    console.log(error);            	
    }
});	
*/
	