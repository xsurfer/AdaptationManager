<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title><s:text name="MyCloud.message"/></title>
    <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/assets/css/pepper-grinder/jquery-ui-1.10.3.custom.css">
    <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/assets/css/pepper-grinder/ui.jqgrid.css">
    <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/assets/css/pepper-grinder/ui.multiselect.css">

  <!--  <script src="${pageContext.request.contextPath}/assets/js/jquery-1.9.1.js" type="text/javascript"></script>  -->
    <script src="${pageContext.request.contextPath}/assets/js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/assets/js/i18n/grid.locale-en.js" type="text/javascript"></script>
    <script type="text/javascript">
    	$.jgrid.no_legacy_api = true;
    	$.jgrid.useJSON = true;
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery.jqGrid.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/assets/js/jquery.xdomainajax.js" type="text/javascript"></script>

<sj:head jqueryui="true" />

    <script type="text/javascript">
                       $(document).ready(function(){
                       jQuery("#listGet").jqGrid({
                                                    datatype: "local",
                                                    height: 250,
                                                    colNames:['Key identifier','Machine', 'Frequency'],
                                                    colModel:[
                                                        {name:'kid',index:'kid', width:600},
                                                        {name:'machine',index:'machine', width:90},
                                                        {name:'freq',index:'freq', width:60, align:"right",sorttype:"int"}
                                                    ],
                                                    caption: "Remote Gets",
                                                    rowNum:10,
                                                    rowList:[10,20,30],
                                                    pager: '#pagerGet',
                                                    sortable: true,
                                                    sortname: 'freq',
                                                    viewrecords: true,
                                                    sortorder: "desc"
                       });
                        jQuery("#listPut").jqGrid({
                                                                           datatype: "local",
                                                                           height: 250,
                                                                           colNames:['Key identifier','Machine', 'Frequency'],
                                                                           colModel:[
                                                                               {name:'kid',index:'kid', width:600},
                                                                               {name:'machine',index:'machine', width:90},
                                                                               {name:'freq',index:'freq', width:60, align:"right",sorttype:"int"}
                                                                           ],
                                                                           caption: "Remote Puts",
                                                                           rowNum:10,
                                                                           rowList:[10,20,30],
                                                                           pager: '#pagerPut',
                                                                           sortable: true,
                                                                           sortname: 'freq',
                                                                           viewrecords: true,
                                                                           sortorder: "desc"
                                              });
                                               jQuery("#listFail").jqGrid({
                                                                                                  datatype: "local",
                                                                                                  height: 250,
                                                                                                  colNames:['Key identifier','Machine', 'Frequency'],
                                                                                                  colModel:[
                                                                                                      {name:'kid',index:'kid', width:600},
                                                                                                      {name:'machine',index:'machine', width:90},
                                                                                                      {name:'freq',index:'freq', width:60, align:"right",sorttype:"int"}
                                                                                                  ],
                                                                                                  caption: "Keys that caused aborts",
                                                                                                  rowNum:10,
                                                                                                  rowList:[10,20,30],
                                                                                                  pager: '#pagerFail',
                                                                                                  sortable: true,
                                                                                                  sortname: 'freq',
                                                                                                  viewrecords: true,
                                                                                                  sortorder: "desc"
                                                                     });
                                                var cntRow = 0;


                                                $.getJSON( "http://cloudtm.ist.utl.pt:3030/topk", function(data){
                                                            var data_length = data["nodeMap"].length;
                                                            for (var i = 0; i < data_length; i++) {
                                                                var machineId = data["nodeMap"][i]["nodeID"];
                                                                var topKMapGets = data["nodeMap"][i]["topKMap"]["REMOTE_GET"];
                                                                $.each(topKMapGets, function(k, v) {
                                                                    var newRow = new Object();
                                                                    newRow["kid"] = k;
                                                                    newRow["machine"] = machineId;
                                                                    newRow["freq"] = v;
                                                                    jQuery("#listGet").jqGrid('addRowData',cntRow+1, newRow);
                                                                    cntRow++;
                                                                });
                                                                var topKMapPuts = data["nodeMap"][i]["topKMap"]["REMOTE_PUT"];
                                                                                                                                $.each(topKMapPuts, function(k, v) {
                                                                                                                                    var newRow = new Object();
                                                                                                                                    newRow["kid"] = k;
                                                                                                                                    newRow["machine"] = machineId;
                                                                                                                                    newRow["freq"] = v;
                                                                                                                                    jQuery("#listPut").jqGrid('addRowData',cntRow+1, newRow);
                                                                                                                                    cntRow++;
                                                                                                                                });
                                                            var topKMapFails = data["nodeMap"][i]["topKMap"]["FAILED"];
                                                                                                                            $.each(topKMapFails, function(k, v) {
                                                                                                                                var newRow = new Object();
                                                                                                                                newRow["kid"] = k;
                                                                                                                                newRow["machine"] = machineId;
                                                                                                                                newRow["freq"] = v;
                                                                                                                                jQuery("#listFail").jqGrid('addRowData',cntRow+1, newRow);
                                                                                                                                cntRow++;
                                                                                                                            });
                                                            }
                                                             jQuery("#listGet").trigger("reloadGrid");
                                                              jQuery("#listPut").trigger("reloadGrid");
                                                            jQuery("#listFail").trigger("reloadGrid");

                                                    });

                       });

                     </script>


</head>

<body>
	<!-- Promo -->
	<div id="col-top"></div>
	<div class="box" id="col">
    
    <div id="ribbon"></div> <!-- /ribbon (design/ribbon.gif) -->
        
    <!-- Screenshot in browser (replace tmp/browser.gif) -->
    <div id="col-browser"></div> 
    
  	<div id="col-text">
        
        <h2>Workload Analysis</h2>

<center>
          <table id="listGet"></table>
          <div id="pagerGet"></div>
          <br>
           <br>
           <table id="listPut"></table>
          <div id="pagerPut"></div>
           <br>
            <br>
          <table id="listFail"></table>
            <div id="pagerFail"></div>

 </center>


	</div> <!-- /col-text -->
    
    </div> <!-- /col -->
    <div id="col-bottom"></div>
    
    <hr class="noscreen">
    <hr class="noscreen">


</body>





</html>



