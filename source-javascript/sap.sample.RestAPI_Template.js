var oScriptMgr;    // Java instance of the IScriptMgr passed by the Script Task Template
var JavaString;
var charset;
var connection;
var baseUrl;
var logonToken;

/* initialize the CustomScript instance settings 
   invoked from the Script Task Template  
*/
var init = function(scriptManager) {
	oScriptMgr = scriptManager;
	mSettings = {
		loadFolderCUID: "AWn5z9mk2IZEvsxkSUaXcMs",	
		loadName: "sap.sample.core,sap.sample.request",
		queryProperties: "",
		queryFilter: "si_id=12",
		resultHeader: "id,name",
		fnExecute: "execute"
	};
	oScriptMgr.setSettings(JSON.stringify(mSettings));
	
	var imports = new JavaImporter(java.util, java.io, java.lang, java.nio.charset);
	with(imports) {
		// classes from imported packages can be accessed by unqualified names
		charset = Charset.forName("UTF-8");
	}
	JavaString = Java.type("java.lang.String");
	HttpURLConnection = Java.type("java.net.HttpURLConnection");
	URL = Java.type("java.net.URL");
	logonToken = null;
	baseUrl = getBaseUrl();
};

/* execute the task 
   invoked from the Script Task Template  
*/
var execute = function(infoObjects) {
	logon();
	response = sendRequest("/raylight/v1" + "/documents", "GET");
	return ExecuteStatus.success;
}

/* add result line */
var addResultDetails = function(id,name) {
	oScriptMgr.addResultDetails(id + "," + name);
}

/* logonRequest */
var logon = function() {
	response = sendRequest("logon/token", "GET");
	content = JSON.parse(response.content);
    content.logonToken = oScriptMgr.getEnterpriseSession().getSerializedSession();
	content.tokenType = "serializedSession";
	response = sendRequest("logon/token", "POST", JSON.stringify(content));
	logonToken = response.headers.get("X-SAP-LogonToken").get(0);
	addResultDetails("X-SAP-LogonToken", logonToken);
}


