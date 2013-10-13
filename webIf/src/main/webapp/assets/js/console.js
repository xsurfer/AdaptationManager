var REST_ALL_LOG = 'http://' + REST_HOST + ':' + REST_PORT +'/log';
var REST_ID_LOG = 'http://' + REST_HOST + ':' + REST_PORT +'/log/';

var last_id = -1;

$(document).ready( function(){
		Retrieve();	
		$("#logbox").empty();
		$("#logbox").scrollTop($("#logbox")[0].scrollHeight);			
});
    
var Polling = function(){
	setTimeout(Retrieve, 5000);
}
    
var Retrieve = function(){
   	if(last_id == -1){
   		url = REST_ALL_LOG;   		
	} else {
		url = REST_ID_LOG + last_id ;		
   	}
	console.log("url: " + url );
	
	$.getJSON( url, function(data){
		Update(data)
		})
	.complete(function(){
    			Polling();
    		})
    .fail(function() { 
    			setTimeout(function(){
            		document.location.reload(true);	
            	}, 5000);
    		});
    	 		
};

var Update = function(json) {
	
	console.log(json); 
	
	$.each(json, function(key,value) {
        console.log("key: " + key);
        console.log("value: " + value);
        
        var $newMessageLog = $( "<div id=" + key + " class='log'/>" ),
  	    newTimestamp = $( "<span class='timestamp'></span>" ),
  	    newMessage = $( "<p class='message'='timestamp'>" + value + "</p>" );
  	 
  	    $newMessageLog.append(newTimestamp);
  	    $newMessageLog.append(newMessage);
  	    $( "#logbox" ).append( $newMessageLog );
  	    $( "#logbox" ).scrollTop($("#logbox")[0].scrollHeight);
  	    if(last_id == -1){
  	    	last_id = key;
  	    } else {
  	    	last_id++;
  	    }
  	    
	});
	console.log("last_id: " + last_id)
	
	
};

