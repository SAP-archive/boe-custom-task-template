package com.sap.afw.task.webi;

import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class WebiUpdateTemplate extends CustomTaskTemplate {

	@Override
	public Class<?> getTaskImpl() {
		return WebiUpdateImpl.class;
	}

	@Override
	public String getTaskTemplateJSONFilename() {
		return "WebiUpdate.json";
	}

}
