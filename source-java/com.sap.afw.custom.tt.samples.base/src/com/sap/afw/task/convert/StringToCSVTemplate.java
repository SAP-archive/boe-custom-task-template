package com.sap.afw.task.convert;

import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class StringToCSVTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return StringToCSVImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "StringToCSV.json";
	}

}
