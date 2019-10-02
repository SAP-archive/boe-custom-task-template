/*
 * Copyright (c) 2010 by SAP AG,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG and its affiliates. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP AG.
 */ 

package com.sap.afw.task.sample;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.sap.afw.helper.BIPJavaSDK;
import com.sap.afw.helper.Framework;
import com.sap.afw.task.sample.IJavaScriptMgr.EXECUTE_STATUS;
import com.sap.afw.task.sample.IJavaScriptMgr.INPUT_VALUES;
import com.sap.afw.task.sample.IJavaScriptMgr.OUTPUT_VALUES;
import com.sap.afw.task.sample.IJavaScriptMgr.SETTINGS;
import com.sap.bong.common.coretask.base.ITaskOutputValue;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class JavaScriptImpl extends CustomTaskImpl {

	private static final ILogger LOG = LoggerManager.getLogger(JavaScriptImpl.class);

	private String resultSummary;
	private String resultDetails;
	private String processSuccess;
	private String processFailure;
	private String processAll;

	private JavaScriptInputValue inputValue;
	private BIPJavaSDK bip;
	
	public JavaScriptImpl(CustomTaskTemplate taskTemplate) {
		super(taskTemplate);
	}

	@Override
	public TASK_STATUS execute() {
		inputValue = (JavaScriptInputValue)getInputValue();
		String scriptName = "unknown";
		try {
			bip = new BIPJavaSDK(getSerializedSession());
			// query the script by id or cuid
			JSONObject parScript = inputValue.paramScript();
			String condition;
			if (parScript.has("cuid")) {
				condition = "si_cuid='" + parScript.getString("cuid") + "'";
			} else {
				condition = "si_id=" + parScript.getString("id") + "";
			}
			IInfoObjects infoObjects = bip.getInfoStore().query("select si_name, si_cuid, si_id, si_files from ci_infoObjects where " + condition);
			IInfoObject infoObject = null;
			if (infoObjects.size() == 1) infoObject = (IInfoObject)infoObjects.get(0);
			if (infoObject == null) {
				resultSummary = "script not found (" + parScript.toString() + ")";
				return TASK_STATUS.failure;					
			}
			scriptName = infoObject.getTitle();
			// execute the script from the infoObject
			return executeScript(infoObject);
		} catch (Exception e) {
			LOG.error(e);
			resultDetails = "script,error" + Framework.ROW_DELIMITER_NEW_LINE + scriptName + "," + 
					e.getLocalizedMessage().replace(Framework.ROW_DELIMITER_CHAR, ":").replace(Framework.COL_DELIMITER, "-").replace(Framework.ROW_DELIMITER_NEW_LINE, " ");
			return TASK_STATUS.failure;
		}
	}

	/**
	 * 
	 * @param infoObject
	 * @return
	 * @throws Exception
	 */
	private TASK_STATUS executeScript(IInfoObject infoObject) throws Exception {
		// init the script engine
		ScriptEngineManager mgr = new ScriptEngineManager();
		String engineName = "nashorn";
		ScriptEngine engine = mgr.getEngineByName(engineName);
		if (engine == null) {
			resultSummary = engineName + " is not available.";
			return TASK_STATUS.failure;					
		}
		// read the script attached to the infoObject
		String script = bip.readFile(infoObject);
		// create an instance of the CustomScript and pass to the Nashorn script
		IJavaScriptMgr customScript = new IJavaScriptMgr(bip.getInfoStore(), bip.getEnterpriseSession());
		customScript.setInputValue(INPUT_VALUES.parameterValue, inputValue.paramCSVValues().toString());
		engine.eval(script);
		Invocable invocable = (Invocable) engine;
	    Object result = invocable.invokeFunction("init", customScript);
	    // load specified additional scripts
		String loadCUID = customScript.getSetting(SETTINGS.loadFolderCUID);
		if (loadCUID.length() > 0) {
			String loadJS = customScript.getSetting(SETTINGS.loadName);
			if (loadJS.length() > 0) {
				String condition = Framework.getInValues(new HashSet<String>(Arrays.asList(loadJS.split(","))));
				IInfoObjects scripts = bip.getInfoStore().query("select si_name, si_cuid, si_id, si_files from ci_infoObjects where si_parent_cuid='" + loadCUID + "' AND si_name in " + condition);
				for(Iterator scriptIt = scripts.iterator(); scriptIt.hasNext(); ) {
					engine.eval(bip.readFile((IInfoObject)scriptIt.next()));
				}
			}
		}

	    // build the cms query and get the infoObjects
	    IInfoObjects workInfoObjects = queryInfoObjects(customScript.getSetting(SETTINGS.queryProperties), customScript.getSetting(SETTINGS.queryFilter));
	    // call the script execute function
		result = invocable.invokeFunction(customScript.getSetting(SETTINGS.fnExecute), workInfoObjects);
		// get the task result information
		resultDetails = customScript.getResultDetails();
		resultSummary = customScript.getResultSummary();
		processSuccess = customScript.getOutputValue(OUTPUT_VALUES.success);
		processFailure = customScript.getOutputValue(OUTPUT_VALUES.failure);
		processAll = customScript.getOutputValue(OUTPUT_VALUES.all);
		if ( ((EXECUTE_STATUS)result).equals(EXECUTE_STATUS.success) ) {
			return TASK_STATUS.success;
		} else 	if ( ((EXECUTE_STATUS)result).equals(EXECUTE_STATUS.partial_success) ) {
			return TASK_STATUS.partial_success;
		}
		return TASK_STATUS.failure;
	}

	/**
	 * 
	 * @param properties
	 * @param filter
	 * @return
	 * @throws SDKException
	 */
	private IInfoObjects queryInfoObjects(String properties, String filter) throws SDKException {
		String cmsQuery = "select " + properties;
		// all Tables
	    cmsQuery += " from CI_INFOOBJECTS, CI_SYSTEMOBJECTS, CI_APPOBJECTS";
	    // build condition with CUIDs / IDs, combine with OR
	    String condition = "";
	    Framework cache = new Framework();
	    cache.buildSets(inputValue.paramInfoObjects());
	    String partCondition = cache.getCondition(Framework.CUID);
	    if (partCondition.length() > 0) {
	    	condition = " where (" + partCondition;
	    }
	    partCondition = cache.getCondition(Framework.ID);
	    if (partCondition.length() > 0) {
	    	if (condition.length() > 0) {
	    		condition += " or " + partCondition + ")";
	    	} else {
		    	condition = " where (" + partCondition + ")";
	    	}
	    } else if (condition.length() > 0) {
	    	condition += ")";
	    }
	    // add script filter to condition, combine with AND if other conditions are already defined
	    partCondition = filter;
	    if (partCondition.length() > 0) {
	    	if (condition.length() > 0) {
	    		condition += " and (" + partCondition + ")";
	    	} else {
		    	condition = " where (" + partCondition + ")";
	    	}
	    }
	    return bip.getInfoStore().query(cmsQuery + condition); 
	}
	
	@Override
	public JSONObject inputValueInstance() throws JSONException {
		return new JavaScriptInputValue(this);
	}

	@Override
	public ITaskOutputValue outputValue() {
		return new ITaskOutputValue() {

			@Override
			public String getValue(String key) {
				if(key.equals("success_list")){
					return processSuccess;
				}
				else if(key.equals("failure_list")){
					return processFailure;
				}
				else if(key.equals("all")){
					return processAll;
				}
				return null;
			}
		};
	}
	
	@Override
	public String resultSummary() {
		return resultSummary;
	}

	@Override
	public String resultDetails() {
		// use CR LF to start new line
		return resultDetails;
	}
	
}

