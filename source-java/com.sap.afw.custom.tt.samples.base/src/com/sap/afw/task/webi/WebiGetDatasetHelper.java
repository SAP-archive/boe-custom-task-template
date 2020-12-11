package com.sap.afw.task.webi;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.sap.afw.helper.BIPJavaSDK;
import com.sap.afw.helper.Framework;
import com.sap.afw.helper.RESTRequest;
import com.sap.bong.common.coretask.base.TaskImplCore.TASK_STATUS;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;

public class WebiGetDatasetHelper extends TaskHelper {

	private WebiGetDatasetInputValue inputValue;

	private boolean worklist;
	private int docId;
	private String csvOutput;
	
	
	public WebiGetDatasetHelper(CustomTaskImpl taskImpl, boolean worklist) throws Exception {
		super(taskImpl);
		this.worklist = worklist;
		inputValue = (WebiGetDatasetInputValue) taskImpl.getInputValue();
	}
	
	private void setDocId() throws SDKException {
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
		IInfoObjects infoObjects = getBIP().getInfoStore().query("select si_name, si_cuid, si_id from ci_infoObjects where " + condition);
		IInfoObject infoObject = null;
		if (infoObjects.size() == 1) infoObject = (IInfoObject)infoObjects.get(0);
		if (infoObject == null) {
			setResultSummary("document not found (" + parDoc.toString() + ")");
			docId = 0;
		}
		docId = infoObject.getID();
	}


	public String getCsvOutput() {
		return csvOutput;
	}
	
	public TASK_STATUS workOnReportTable() throws Exception {
		setDocId();
		if (docId == 0) {
			return TASK_STATUS.failure;					
		}
		
		JSONArray reportTableList = inputValue.paramReportTable();
		setResultSummary("processed ");
		for (int i=0; i<reportTableList.length(); i++) {
			JSONObject workObj = reportTableList.getJSONObject(i);
			String report = workObj.getString(WebiGetDatasetInputValue.REPORT).toString();
			String table = workObj.getString(WebiGetDatasetInputValue.TABLE).toString();
			WebIReports docReports = new WebIReports(getRest());
			
			// get the list of reports from the WebI document
			Map<String, Integer> reportList = docReports.requestReportList(docId);
			if (reportList == null) return TASK_STATUS.failure; 
			if (reportList.get(report) == null) return TASK_STATUS.failure; 
			// get the tables from the report
			Map<String, Integer> reportElementList = docReports.requestReportElementsList(docId, reportList.get(report), "VTable");
			if (reportElementList == null) return TASK_STATUS.failure; 
			if (reportElementList.get(table) == null) return TASK_STATUS.failure; 
			
			JSONObject dataSet = docReports.requestReportElementDataset(docId, reportList.get(report), reportElementList.get(table));
			Map<Integer, JSONObject> rows = docReports.getDatasetRows(dataSet);
			addResultSummary(report + "." + table + " rows:" + rows.values().size());

			if (worklist) {
				Map<Integer, JSONObject> metadata = docReports.getDatasetMetadata(dataSet);
				processWorklist(metadata, rows);
			} else {
				processDataset(report, table, rows);
			}
		}
		return TASK_STATUS.success;
	}

	private void processWorklist(Map<Integer, JSONObject> metadata, Map<Integer, JSONObject> rows) {
		StringBuilder header = new StringBuilder();
        String separator = "";
		for (JSONObject element : metadata.values()) {
			header.append(separator + element.getString("$"));
			separator = Framework.COL_DELIMITER;
		}
		StringBuilder values = new StringBuilder();
		values.append(header.toString() + Framework.ROW_DELIMITER_NEW_LINE);
		setResultSummary("header: " + header.toString());

		for (JSONObject element : rows.values()) {
			JSONArray row = element.getJSONArray("value");
	        separator = "";
			for (int i=0; i<row.length(); i++) {
				values.append(separator + row.get(i).toString());
				separator = Framework.COL_DELIMITER;
			}
			values.append(Framework.ROW_DELIMITER_NEW_LINE);
		}
		csvOutput = values.toString();
	}
	
	private void processDataset(String report, String table, Map<Integer, JSONObject> rows) {
		csvOutput = "report" + Framework.COL_DELIMITER +
				"table" + Framework.COL_DELIMITER + 
				"row" + Framework.COL_DELIMITER + 
				"values" + Framework.ROW_DELIMITER_NEW_LINE;

		int row = 0;
		for (JSONObject element : rows.values()) {
			JSONArray values = JSONHelper.getJSONArrayAlways(element, "value");
			csvOutput += report + Framework.COL_DELIMITER + table + Framework.COL_DELIMITER + 
					     ++row + Framework.COL_DELIMITER + 
						 Framework.replaceDelimiterCharacter(values.toString()) + Framework.ROW_DELIMITER_NEW_LINE;
		}
	}
	
}
