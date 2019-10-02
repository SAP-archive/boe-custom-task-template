package com.sap.afw.task.compare;


import org.json.JSONException;

import com.sap.afw.helper.Framework;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskInputValue;

public class CompareInputValue extends CustomTaskInputValue {

	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String COMPARE_CONDITION = "compare_condition";
	public static final String COMPARE_EQUAL = "equal";
	public static final String COMPARE_NOT_EQUAL = "not_equal";
	
	public CompareInputValue(CustomTaskImpl taskImpl) throws JSONException {
		super(taskImpl);
	}

	public String paramSource() throws JSONException {
		if (get(SOURCE) instanceof String) {
			return getString(SOURCE);
		}
		return Framework.toCSVwithMetadata(this, SOURCE);
	}
	
	public String paramTarget() throws JSONException {
		if (get(TARGET) instanceof String) {
			return getString(TARGET);
		}
		return Framework.toCSVwithMetadata(this, TARGET);
	}
	
	public boolean paramSuccessOnEqual() throws JSONException {
		return getString(COMPARE_CONDITION).equals(COMPARE_EQUAL);
	}
	
}
