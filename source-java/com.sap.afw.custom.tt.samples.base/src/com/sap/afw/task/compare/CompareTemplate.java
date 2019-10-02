package com.sap.afw.task.compare;


import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class CompareTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return CompareImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "CompareString.json";
	}

}
