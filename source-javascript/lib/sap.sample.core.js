// simplify usage of Java classes to avoid that package must be specified
var Settings = Java.type("com.sap.afw.task.sample.IJavaScriptMgr.SETTINGS");
var InputValues = Java.type("com.sap.afw.task.sample.IJavaScriptMgr.INPUT_VALUES");
var OutputValues = Java.type("com.sap.afw.task.sample.IJavaScriptMgr.OUTPUT_VALUES");
var ExecuteStatus = Java.type("com.sap.afw.task.sample.IJavaScriptMgr.EXECUTE_STATUS");
// JSON object reflecting the parameter value passed to the Script Task Template
var parameterValue = JSON.parse(oScriptMgr.getInputValue(InputValues.parameterValue));
