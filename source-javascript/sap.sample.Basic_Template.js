var oScriptMgr;    // Java instance of the IScriptMgr passed by the Script Task Template

/* initialize the CustomScript instance settings 
   invoked from the Script Task Template  
*/
var init = function(scriptManager) {
	oScriptMgr = scriptManager;
	mSettings = {
		loadFolderCUID: "AWn5z9mk2IZEvsxkSUaXcMs",	
		loadName: "sap.sample.core",
		queryProperties: "",
		queryFilter: "si_id=12",
		resultHeader: "id,name",
		fnExecute: "execute"
	};
	oScriptMgr.setSettings(JSON.stringify(mSettings));
};

/* execute the task 
   invoked from the Script Task Template  
*/
var execute = function(infoObjects) {
	// write the input parameter values to the result details
	addResultDetails("parameterValues", parameterValue.length);
	for (i=0; i<parameterValue.length; i++) {
		for (parameter in parameterValue[i]) {
			addResultDetails(parameter, parameterValue[i][parameter]);
		}
	}
	// work on the InfoObjects
	var size = infoObjects.size();
	oScriptMgr.setResultSummary("processed 0 of " + size);
	for (i=0; i<size; i++) {
		var infoObject = infoObjects.get(i);
		addResultDetails(infoObject.getID(),infoObject.getTitle());
		oScriptMgr.addOutputValue(OutputValues.all, infoObject.getID());
		oScriptMgr.addOutputValue(OutputValues.success, infoObject.getID());
		oScriptMgr.setResultSummary("processed " + (i + 1) + " of " + size);
	}
	return ExecuteStatus.success;
}

/* add result line */
var addResultDetails = function(id,name) {
	oScriptMgr.addResultDetails(id + "," + name);
}