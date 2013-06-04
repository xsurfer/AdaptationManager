var resources = ["scale","rep_degree","rep_protocol","data_placement"];


$(document).ready( 
    		
    		function(){
    			init(); 
    			$('.fancybox').fancybox();
    			
    			$('#scale').ajaxForm(function() { 
                    alert("Thank you for your comment!"); 
                }); 
    		}    
    		    		
);

function init(){

	init_radio();
}

function init_radio(){	

	var length = resources.length, element = null;
	for (var i = 0; i < length; i++) {
		
		element = resources[i];
		var $radios = $('input:radio[name='+element+'_tuning]');
				
		// INIT		
	    if($radios.is(':checked') === false) {
	        $radios.filter('[value=manual]').prop('checked', true);
	        disableFieldset($radios);
	    }
	    
	    // LISTENER
	    $radios.change(function(){
	    	disableFieldset($(this));
	    });	      
	}	
}

function disableFieldset($radio){
	console.log($radio.val()); // manual o self	
	console.log($radio.attr("name"));
	// seleziono tutte le fieldset che non si chiamano in quel modo e le disattivo
	
	// name + '_' + value
	
	var fieldsetName = $radio.attr("name") + '_';
	var enabled = disabled = fieldsetName;
	if($radio.attr("value")=="self"){
		enabled += 'self';
		disabled += 'manual';
	} else {
		enabled += 'manual';
		disabled += 'self';
	}
	
	console.log("enabled: " + enabled);
	console.log("disabled: " + disabled);
	
	$('fieldset[name="'+ disabled +'"]').children().attr("disabled", "disabled");
	$('fieldset[name="'+ enabled +'"]').children().removeAttr("disabled");

}