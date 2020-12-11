package com.sap.afw.task.webi;

import com.sap.afw.helper.BIPJavaSDK;
import com.sap.afw.helper.RESTRequest;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;

public abstract class TaskHelper {

	private BIPJavaSDK bip;
	private RESTRequest rest;
	private String resultSummary;


	public TaskHelper(CustomTaskImpl taskImpl)  throws Exception {
		bip = new BIPJavaSDK(taskImpl.getSerializedSession());
		rest = new RESTRequest();
		rest.connect(bip);
	}
	
	public BIPJavaSDK getBIP() {
		return bip;
	}
	
	public RESTRequest getRest() {
		return rest;
	}
	
	public void setResultSummary(String value) {
		resultSummary = value;
	}
	
	public void addResultSummary(String value) {
		resultSummary += value;
	}
	
	public String getResultSummary() {
		return resultSummary;
	}
}
