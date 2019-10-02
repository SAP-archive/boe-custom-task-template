var oScriptMgr;    // Java instance of the IScriptMgr passed by the Script Task Template
var charset;
var JavaString;

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
		execute: "execute"
	};
	oScriptMgr.setSettings(JSON.stringify(mSettings));
	// a JavaImporter can be used as a "with" expression object
	var imports = new JavaImporter(java.util, java.io, java.lang, java.nio.charset);
	with(imports) {
		// classes from imported packages can be accessed by unqualified names
		charset = Charset.forName("UTF-8");
	}
	JavaString = Java.type("java.lang.String");
};

/* execute the task 
   invoked from the Script Task Template  
*/
var execute = function(infoObjects) {
	// write the input parameter values to the result details
	fileInfoObjects = oScriptMgr.getInfoStore().newInfoObjectCollection();
	boPluginMgr = oScriptMgr.getInfoStore().getPluginMgr();
	//filePlugin = boPluginMgr.getPluginInfo("CrystalEnterprise.Excel");
	filePlugin = boPluginMgr.getPluginInfo("CrystalEnterprise.Txt");
	fileInfoobject = fileInfoObjects.add(filePlugin);
	fileInfoobject.setParentID(7083);  // _logs
	fileInfoobject.setTitle("scriptLog");
	files = fileInfoobject.getFiles();
    //streamFile = files.addStreamingFile(null, "csv", "fileName", null);
    streamFile = files.addStreamingFile(null, "txt", "fileName", null);
	myString = new JavaString("Header,col2;value1,v1;value2,v2");
	ByteArrayInputStream = Java.type("java.io.ByteArrayInputStream");
	inputStream = new ByteArrayInputStream(myString.getBytes(charset));
	streamFile.putContent(inputStream);
    streamFile.commit();
	oScriptMgr.getInfoStore().commit(fileInfoObjects);
	return ExecuteStatus.success;
}

/* add result line */
var addResultDetails = function(id,name) {
	oScriptMgr.addResultDetails(id + "," + name);
}