package com.sap.afw.task.sample;

import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class JavaScriptTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return JavaScriptImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "JavaScript.json";
	}

}
