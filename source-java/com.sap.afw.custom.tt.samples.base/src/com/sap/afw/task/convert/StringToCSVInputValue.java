package com.sap.afw.task.convert;

import org.json.JSONException;

import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class StringToCSVInputValue extends CustomTaskInputValue {

	// this should match the JSON task template
	private static enum PARAMETER {
		string_input
	}
	
	public StringToCSVInputValue(CustomTaskImpl taskTemplate) throws JSONException {
		super(taskTemplate);
	}

	public String paramString() {
		return optString(PARAMETER.string_input.toString());
	}

}
