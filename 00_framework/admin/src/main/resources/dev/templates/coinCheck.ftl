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
		<h3 style="text-align:center;">币种RPC接口健康检查报告</h3>
	    <hr>
		<div>
		    <h4 style="color:#FF0000;">需要注意的币种：</h4>
		    <table class="gridtable">
		    	<tr><th>币种</th><th>账户类型</th><th>RPC</th><th>区块高度</th></tr>
		    	<#list noticeList as coin>
		    		<tr>
		    			<td style="color:#FF0000;">${coin.unit}</td>
		    			<#if coin.accountType==0>
				        	<td>地址模式</td>
				        <#else>
				        	<td style="color: rgb(69, 184, 84);">账户模式</td>
				        </#if>
		    			<#if coin.enableRpc=="IS_TRUE">
				        	<td style="color: rgb(69, 184, 84);">开启</td>
				        <#else>
				        	<td style="color: #FF0000;">关闭</td>
				        </#if>
		    			<td style="color:#FF0000;">${coin.blockHeight}</td>
		    		</tr>
				</#list>
		    </table>
		</div>
		<br/><br/>
		<div>
		    <h4>区块高度变化币种：</h4>
		    <table class="gridtable">
		    	<tr><th>币种</th><th>账户类型</th><th>RPC</th><th>区块高度</th></tr>
		    	<#list changeList as coin>
		    		<tr>
		    			<td>${coin.unit}</td>
		    			<#if coin.accountType==0>
				        	<td>地址模式</td>
				        <#else>
				        	<td style="color: rgb(69, 184, 84);">账户模式</td>
				        </#if>
				        
				        
		    			<#if coin.enableRpc=="IS_TRUE">
				        	<td style="color: rgb(69, 184, 84);">开启</td>
				        <#else>
				        	<td style="color: #FF0000;">关闭</td>
				        </#if>
		    			<td>${coin.blockHeight}</td>
		    		</tr>
				</#list>
		    </table>
		</div>
		<br/><br/>
		<div>
		    <h4>区块高度未变化币种：</h4>
		    <table class="gridtable">
		    	<tr><th>币种</th><th>账户类型</th><th>RPC</th><th>区块高度</th></tr>
		    	<#list nochangeList as coin>
		    		<tr>
		    			<td>${coin.unit}</td>
		    			<#if coin.accountType==0>
				        	<td>地址模式</td>
				        <#else>
				        	<td style="color: rgb(69, 184, 84);">账户模式</td>
				        </#if>
				        
				        
		    			<#if coin.enableRpc=="IS_TRUE">
				        	<td style="color: rgb(69, 184, 84);">开启</td>
				        <#else>
				        	<td style="color: #FF0000;">关闭</td>
				        </#if>
		    			<td>${coin.blockHeight}</td>
		    		</tr>
				</#list>
		    </table>
		</div>
	</body>
</html>