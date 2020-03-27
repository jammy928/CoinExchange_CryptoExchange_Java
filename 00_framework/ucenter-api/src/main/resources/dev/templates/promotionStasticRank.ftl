<html>
	<head>
	 <meta charset="UTF-8">
	 <title>系统通知</title>
	</head>
	<style type="text/css">
	    table.gridtable {
	        font-family: verdana,arial,sans-serif;
	        font-size:11px;
	        color:#333333;
	        border-width: 1px;
	        border-color: #666666;
	        border-collapse: collapse;
	        width: 100%;
	    }
	    table.gridtable tr th {
	        border-width: 1px;
	        padding: 8px;
	        border-style: solid;
	        border-color: #666666;
	        background-color: #dedede;
	    }
	    table.gridtable td {
	        border-width: 1px !important;
	        padding: 8px;
	        border-style: solid !important;
	        border-color: #666666 !important;
	        background-color: #ffffff;
	        width: 25%;
	        text-align: center;
	    }
	</style>
	<body>
		<h3 style="text-align:center;">邀请排行榜生成报告</h3>
	    <hr>
	    <div>
		    <h4>邀请人数排名TOP20：</h4>
		    <table class="gridtable">
		    	<tr><th>排名</th><th>用户</th><th>邀请人数</th><th>机器人</th></tr>
		    	<#list topInviteList as vo>
		    		<tr>
		    			<td>${vo_index+1}</td>
		    			<td>${vo.userIdentify}</td>
		    			<td>${vo.levelOne}</td>
		    			<#if vo.isRobot==0>
				        	<td>否</td>
				        <#else>
				        	<td style="color:#FF0000;">是</td>
				        </#if>
		    		</tr>
				</#list>
		    </table>
		</div>
		<br/><br/>
	</body>
</html>