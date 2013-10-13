<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
	<title>Console</title>

	<link href="${pageContext.request.contextPath}/assets/css/per_page/console.css" type="text/css" media="screen" rel="stylesheet" />

	<script type="text/javascript">
	var REST_HOST = '<s:property value="getRestHost()" />';
	var REST_PORT = '<s:property value="getRestPort()" />';
	</script>

	<script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
	<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/console.js"></script>

   
</head>

<body>

<div id="logbox">
	<div id="a" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code The jQuery team is constantly working to improve the code
		</p>
	</div>
	
	<div id="b" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code
		</p>
	</div>

	<div id="c" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code The jQuery team is constantly working to improve the code
		</p>
	</div>
	
	<div id="d" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code
		</p>
	</div>

	<div id="e" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code The jQuery team is constantly working to improve the code
		</p>
	</div>
	
	<div id="f" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code
		</p>
	</div>

	<div id="g" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code The jQuery team is constantly working to improve the code
		</p>
	</div>
	
	<div id="h" class="log">
		<span class="timestamp">12:34:09</span>
		<p class="message">
			The jQuery team is constantly working to improve the code
		</p>
	</div>

</div>
    
</body>
</html>



