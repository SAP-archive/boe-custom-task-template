package com.sap.afw.task.sample;

import org.json.JSONObject;

import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.occa.infostore.IInfoStore;
import com.sap.bong.common.coretask.util.CSVHandler;

public class IJavaScriptMgr {
	
	public static enum SETTINGS {
		loadFolderCUID,
		loadName,
		queryProperties,
		queryFilter,
		resultHeader,
		fnExecute
	}
	
	public static enum INPUT_VALUES {
		parameterValue
	}

	public static enum OUTPUT_VALUES {
		all, success, failure
	}

	public static enum EXECUTE_STATUS {
		success, partial_success, failure 
	}
	
	private IInfoStore infoStore;
	private IEnterpriseSession eSession;
	
	private JSONObject settings;
	private JSONObject inputValues;
	private JSONObject outputValues;
	
	private String resultSummary = "ToDo";  // String
	private String resultDetails = "";  // CSV
	
	/**
	 * 
	 * @param infoStore
	 * @param eSession
	 */
	public IJavaScriptMgr(IInfoStore infoStore, IEnterpriseSession eSession) {
		this.infoStore = infoStore;
		this.eSession = eSession;
		this.inputValues = new JSONObject();
		this.outputValues = new JSONObject();
		this.outputValues.put(OUTPUT_VALUES.all.toString(), "id;");
		this.outputValues.put(OUTPUT_VALUES.success.toString(), "id;");
		this.outputValues.put(OUTPUT_VALUES.failure.toString(), "id;");
	}

	/**
	 * 
	 * @return
	 */
	public IInfoStore getInfoStore() {
		return infoStore;
	}
	
	/**
	 * 
	 * @return
	 */
	public IEnterpriseSession getEnterpriseSession() {
		return eSession;
	}

	/*
	public ByteArrayInputStream getByteArrayInputStream(String value, String charset) throws UnsupportedEncodingException {
		return new ByteArrayInputStream(value.getBytes(charset));
	}
	*/
	
	/**
	 * 
	 * @param mSettings
	 */
	public void setSettings(String mSettings) {
		this.settings = new JSONObject(mSettings);
		// use SI_ID as default property
		if (getSetting(SETTINGS.queryProperties).length() == 0) {
			this.settings.put(SETTINGS.queryProperties.toString(), "SI_ID");
		}
		// init resultDetails with header information
		this.resultDetails = this.settings.get(SETTINGS.resultHeader.toString()) + CSVHandler.ROW_DELIMITER_NEW_LINE;
	}

	/**
	 * 
	 * @param setting
	 * @return
	 */
	public String getSetting(SETTINGS setting) {
		if (!this.settings.has(setting.toString())) return "";
		return this.settings.getString(setting.toString());
	}
	
	/**
	 * 
	 * @param parameter
	 * @return
	 */
	public String getInputValue(INPUT_VALUES parameter) {
		return this.inputValues.getString(parameter.toString());
	}

	/**
	 * 
	 * @param parameter
	 * @param value
	 */
	public void setInputValue(INPUT_VALUES parameter, String value) {
		this.inputValues.put(parameter.toString(), value);
	}

	/**
	 * 
	 * @param output
	 * @return
	 */
	public String getOutputValue(OUTPUT_VALUES output) {
		return this.outputValues.getString(output.toString());
	}
	
	/**
	 * 
	 * @param output
	 * @param id
	 */
	public void addOutputValue(OUTPUT_VALUES output, String id) {
		this.outputValues.put(output.toString(), this.outputValues.getString(output.toString()) + id + ";");
	}

	/**
	 * 
	 * @return
	 */
	public String getResultDetails() {
		return this.resultDetails;
	}
	
	/**
	 * 
	 * @param value
	 */
	public void addResultDetails(String value) {
		this.resultDetails += value + CSVHandler.ROW_DELIMITER_NEW_LINE;;
	}

	/**
	 * 
	 * @return
	 */
	public String getResultSummary() {
		return this.resultSummary;
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setResultSummary(String value) {
		this.resultSummary = value;
	}

}
