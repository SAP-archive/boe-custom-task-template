package com.sap.afw.task.sample;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class JavaScriptInputValue extends CustomTaskInputValue {

	// this should match the JSON task template
	public static final String SCRIPT = "script";
	public static final String INFOOBJECTS = "infoobjects";
	public static final String CSV_PARAM = "parameter_value";
	
	public JavaScriptInputValue(CustomTaskImpl taskImpl) throws JSONException {
		super(taskImpl);
	}

	public JSONObject paramScript() {
		// from JUnit test will get a string
		if (get(SCRIPT) instanceof String) {
			return new JSONArray(get(SCRIPT).toString()).getJSONObject(0);
		};
		return getJSONArray(SCRIPT).getJSONObject(0);
	}

	public JSONArray paramInfoObjects() {
		// from JUnit test will get a string
		if (get(INFOOBJECTS) instanceof String) {
			return new JSONArray(get(INFOOBJECTS).toString());
		};
		return optJSONArray(INFOOBJECTS);
	}

	public JSONArray paramCSVValues() {
		if (!has(CSV_PARAM)) return new JSONArray("[{}]");
		return optJSONArray(CSV_PARAM);
	}

}
