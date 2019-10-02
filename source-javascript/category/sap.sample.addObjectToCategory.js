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
		resultHeader: "id,name",
		fnExecute: "execute"
	};
	oScriptMgr.setSettings(JSON.stringify(mSettings));
};

/* execute the task 
   invoked from the Script Task Template  
*/
var execute = function(infoObjects) {
	// query the categories
	var categories = queryCategories();
	// add infoObjects to the category
	var size = infoObjects.size();
	oScriptMgr.setResultSummary("processed 0 of " + size);
	for (i=0; i<size; i++) {
		var infoObject = infoObjects.get(i);
		// add infoObject to all categories
		for (j=0; j<categories.size(); j++) {
			categories.get(j).getDocuments().add(parseInt(infoObject.getID()));
		}
		addResultDetails(infoObject.getID(),infoObject.getTitle());
		oScriptMgr.addOutputValue(OutputValues.all, infoObject.getID());
		oScriptMgr.addOutputValue(OutputValues.success, infoObject.getID());
		oScriptMgr.setResultSummary("processed " + (i + 1) + " of " + size);
	}
	oScriptMgr.getInfoStore().commit(categories);
	return ExecuteStatus.success;
}

/* query the specified categories */
var queryCategories = function() {
	separator = "";
	condition = "";
	for (i=0; i<parameterValue.length; i++) {
		addResultDetails("add to category", parameterValue[i].category);
		condition += separator + "'" + parameterValue[i].category + "'";
		separator = ",";
	}
	query = "select si_name, si_documents from ci_infoobjects where si_kind='category' and si_name in (" + condition + ")";
	categories = oScriptMgr.getInfoStore().query(query);
	addResultDetails("category query entries", categories.size());
	return categories;
}

/* add result line */
var addResultDetails = function(id,name) {
	oScriptMgr.addResultDetails(id + "," + name);
}
