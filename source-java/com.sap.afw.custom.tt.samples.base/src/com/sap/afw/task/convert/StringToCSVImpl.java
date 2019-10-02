/*
 * Copyright (c) 2010 by SAP AG,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG and its affiliates. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP AG.
 */ 

package com.sap.afw.task.convert;

import org.json.JSONException;
import org.json.JSONObject;

import com.sap.bong.common.coretask.base.ITaskOutputValue;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class StringToCSVImpl extends CustomTaskImpl {

	public StringToCSVImpl(CustomTaskTemplate taskTemplate) {
		super(taskTemplate);
	}

	private StringToCSVInputValue inputValue;
	private String[] header;
	private String[] rows;

	@Override
	public TASK_STATUS execute() {
		inputValue = (StringToCSVInputValue) super.getInputValue();
		rows = inputValue.paramString().split(";");
		header = rows[0].split(",");
		return TASK_STATUS.success;
	}

	@Override
	public JSONObject inputValueInstance() throws JSONException {
		return new StringToCSVInputValue(this);
	}


	@Override
	public ITaskOutputValue outputValue() {
		return new ITaskOutputValue() {

			@Override
			public String getValue(String key) {
				if (key.startsWith("csv_output")) {
					return inputValue.paramString();
				}
				return null;
			}
		};
	}

	@Override
	public String resultSummary() {
		return "columns: " + header.length + ", rows: " + (rows.length - 1);
	}

	@Override
	public String resultDetails() {
		// use CR LF to start new line
		StringBuffer results = new StringBuffer();
		String separator = "";
		for (int i=0; i<header.length; i++) {
			results.append(separator + header[i]);
			separator = ",";
		}
		for (int i=1; i<rows.length; i++) {
			results.append("\r\n" + rows[i]);
		}
		return results.toString();
	}
}

