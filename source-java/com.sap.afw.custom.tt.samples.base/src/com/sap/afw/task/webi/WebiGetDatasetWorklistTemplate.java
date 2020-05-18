package com.sap.afw.task.webi;

import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class WebiGetDatasetWorklistTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return WebiGetDatasetWorklistImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "WebiGetDatasetWorklist.json";
	}

}
