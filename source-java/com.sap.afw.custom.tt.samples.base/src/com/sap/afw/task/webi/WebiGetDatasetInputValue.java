package com.sap.afw.task.webi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class WebiGetDatasetInputValue extends CustomTaskInputValue {

	// this should match the JSON task template
	private static enum PARAMETER {
		webi_doc, report_table
	}
	public static final String REPORT = "report";
	public static final String TABLE = "table";

	
	public WebiGetDatasetInputValue(CustomTaskImpl taskTemplate) throws JSONException {
		super(taskTemplate);
	}

	public JSONObject paramDocument() {
		// from JUnit test will get a string
		if (get(PARAMETER.webi_doc.toString()) instanceof String) {
			return new JSONArray(get(PARAMETER.webi_doc.toString()).toString()).getJSONObject(0);
		};
		return getJSONArray(PARAMETER.webi_doc.toString()).getJSONObject(0);

	}
	
	public JSONArray paramReportTable() {
		return optJSONArray(PARAMETER.report_table.toString());
	}

}
