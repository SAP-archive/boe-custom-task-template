package com.sap.afw.task.webi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class WebiUpdateInputValue extends CustomTaskInputValue {

	// this should match the JSON task template
	private static enum PARAMETER {
		webi_doc
	}

	
	public WebiUpdateInputValue(CustomTaskImpl taskTemplate) throws JSONException {
		super(taskTemplate);
	}

	public JSONArray paramDocuments() {
		// from JUnit test will get a string
		if (get(PARAMETER.webi_doc.toString()) instanceof String) {
			return new JSONArray(get(PARAMETER.webi_doc.toString()).toString());
		};
		return getJSONArray(PARAMETER.webi_doc.toString());
	}
	

}
