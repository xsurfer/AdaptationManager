<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">

    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->

    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div>

  	<div id="col-text">

        <h2>Workload and Performance Monitor</h2>
        <div id="container">
       <!-- <iframe src="http://cloudtm.ist.utl.pt:8084/index.php?rootFolder=csv style="width: 100%; height: 900px"></iframe>    -->
       <iframe src="http://cloudtm.ist.utl.pt:8084/index.php?rootFolder=csv" frameborder="0" style="overflow:hidden;height:100%;width:100%" height="100%" width="100%"></iframe>
	   	<!-- <h2 id="slogan"><span><s:property value="message"/></span></h2> -->

      	<!--  <a href="${pageContext.request.contextPath}/registration.jsp">Register</a>  -->
         </div>

	</div> <!-- /col-text -->

    </div> <!-- /col -->
    <div id="col-bottom"></div>

    <hr class="noscreen">
    <hr class="noscreen">
</body>
</html>