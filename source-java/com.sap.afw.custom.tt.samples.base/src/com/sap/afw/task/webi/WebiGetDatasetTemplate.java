package com.sap.afw.task.webi;

import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class WebiGetDatasetTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return WebiGetDatasetImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "WebiGetDataset.json";
	}

}
