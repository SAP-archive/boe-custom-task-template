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
		queryFilter: "",
		resultHeader: "id,name,disable",
		fnExecute: "execute"
	};
	oScriptMgr.setSettings(JSON.stringify(mSettings));
};

/* execute the task 
   invoked from the Script Task Template  
*/
var execute = function(infoObjects) {
	// work on the InfoObjects
	var size = infoObjects.size();
	oScriptMgr.setResultSummary("processed 0 of " + size);
	for (i=0; i<size; i++) {
		var infoObject = infoObjects.get(i);
		flag = Boolean(parameterValue[0].disable === "true");
		infoObject.setDisabled(flag);
		addResultDetails(infoObject.getID(),infoObject.getTitle(),flag);
		oScriptMgr.addOutputValue(OutputValues.all, infoObject.getID());
		oScriptMgr.addOutputValue(OutputValues.success, infoObject.getID());
		oScriptMgr.setResultSummary("processed " + (i + 1) + " of " + size);
	}
	oScriptMgr.getInfoStore().commit(infoObjects);
	return ExecuteStatus.success;
}

/* add result line */
var addResultDetails = function(id,name) {
	oScriptMgr.addResultDetails(id + "," + name + "," + flag);
}
