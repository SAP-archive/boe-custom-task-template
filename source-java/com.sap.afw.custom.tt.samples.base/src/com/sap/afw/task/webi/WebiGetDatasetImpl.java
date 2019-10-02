/*
 * Copyright (c) 2010 by SAP AG,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG and its affiliates. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP AG.
 */ 

package com.sap.afw.task.webi;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.sap.afw.helper.BIPJavaSDK;
import com.sap.afw.helper.Framework;
import com.sap.afw.helper.RESTRequest;
import com.sap.bong.common.coretask.base.ITaskOutputValue;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class WebiGetDatasetImpl extends CustomTaskImpl {

	private static final ILogger LOG = LoggerManager.getLogger(WebiGetDatasetImpl.class);
			
	public WebiGetDatasetImpl(CustomTaskTemplate taskTemplate) {
		super(taskTemplate);
	}

	private WebiGetDatasetInputValue inputValue;
	private BIPJavaSDK bip;
	private String resultSummary;

	private String csvOutput;
	private int rows=0;


	@Override
	public TASK_STATUS execute() {
		
		csvOutput = "report" + Framework.COL_DELIMITER +
					"table" + Framework.COL_DELIMITER + 
					"row" + Framework.COL_DELIMITER + 
					"values" + Framework.ROW_DELIMITER_NEW_LINE;

		try {
			inputValue = (WebiGetDatasetInputValue) super.getInputValue();
			bip = new BIPJavaSDK(getSerializedSession());
			RESTRequest rest = new RESTRequest();
			rest.connect(bip);

			// query the webi document by id or cuid
			JSONObject parDoc = inputValue.paramDocument();
			String condition = "";
			if (parDoc.has("cuid")) {
				if (parDoc.getString("cuid").length() > 0) {
					condition = "si_cuid='" + parDoc.getString("cuid") + "'";
				}
			}
			if ( (condition.length() == 0) && (parDoc.has("id")) ) {
				condition = "si_id=" + parDoc.getString("id") + "";
			}
			IInfoObjects infoObjects = bip.getInfoStore().query("select si_name, si_cuid, si_id from ci_infoObjects where " + condition);
			IInfoObject infoObject = null;
			if (infoObjects.size() == 1) infoObject = (IInfoObject)infoObjects.get(0);
			if (infoObject == null) {
				resultSummary = "document not found (" + parDoc.toString() + ")";
				return TASK_STATUS.failure;					
			}
			int docId = infoObject.getID();
			
			JSONArray reportTableList = inputValue.paramReportTable();
			resultSummary = "processed ";
			for (int i=0; i<reportTableList.length(); i++) {
				JSONObject workObj = reportTableList.getJSONObject(i);
				String report = workObj.getString(WebiGetDatasetInputValue.REPORT).toString();
				String table = workObj.getString(WebiGetDatasetInputValue.TABLE).toString();
				WebIReports docReports = new WebIReports(rest);
				
				// get the list of reports from the WebI document
				Map<String, Integer> reportList = docReports.requestReportList(docId);
				if (reportList == null) return TASK_STATUS.failure; 
				if (reportList.get(report) == null) return TASK_STATUS.failure; 
				// get the tables from the report
				Map<String, Integer> reportElementList = docReports.requestReportElementsList(docId, reportList.get(report), "VTable");
				if (reportElementList == null) return TASK_STATUS.failure; 
				if (reportElementList.get(table) == null) return TASK_STATUS.failure; 
				
				Map<Integer, JSONObject> resultSet = docReports.requestReportElementDataset(docId, reportList.get(report), reportElementList.get(table));
				int row = 0;
				for (JSONObject element : resultSet.values()) {
					JSONArray values = JSONHelper.getJSONArrayAlways(element, "value");
					csvOutput += report + Framework.COL_DELIMITER + table + Framework.COL_DELIMITER + 
							     ++row + Framework.COL_DELIMITER + 
								 Framework.replaceDelimiterCharacter(values.toString()) + Framework.ROW_DELIMITER_NEW_LINE;
				}
				resultSummary += report + "." + table + " rows:" + resultSet.values().size();

			}
			return TASK_STATUS.success;

		} catch (Exception e) {
			LOG.error(e);
			return TASK_STATUS.failure;
		}
	}

	@Override
	public JSONObject inputValueInstance() throws JSONException {
		return new WebiGetDatasetInputValue(this);
	}


	@Override
	public ITaskOutputValue outputValue() {
		return new ITaskOutputValue() {

			@Override
			public String getValue(String key) {
				if (key.startsWith("csv_output")) {
					return csvOutput;
				}
				return null;
			}
		};
	}

	@Override
	public String resultSummary() {
		return resultSummary;
	}

	@Override
	public String resultDetails() {
		// use CR LF to start new line
		//return "col1,col2,col3\r\n,v11,v12,v13\r\n,v21,v22,v23";
		return csvOutput; //resultDetails;
	}
}

