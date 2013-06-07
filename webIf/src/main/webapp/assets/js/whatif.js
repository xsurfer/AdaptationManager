var REST_GET_VALUES = 'http://' + REST_HOST + ':' + REST_PORT +'/whatif/values';
var REST_SET_SCALE = 'http://' + REST_HOST + ':' + REST_PORT +'/scale';


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
    			
    		}
    	);


function retrieveValues(){
	console.log(REST_GET_VALUES);
	$.getJSON( REST_GET_VALUES, function(data){
		alert("ricevuto");
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


