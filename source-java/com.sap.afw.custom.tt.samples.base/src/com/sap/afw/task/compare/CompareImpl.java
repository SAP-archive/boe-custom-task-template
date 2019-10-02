package com.sap.afw.task.compare;


import org.json.JSONException;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.sap.bong.common.coretask.base.ITaskOutputValue;
import com.sap.bong.common.coretask.base.TaskInputValue;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class CompareImpl extends CustomTaskImpl {

	private static final ILogger LOG = LoggerManager.getLogger(CompareImpl.class);

	private TASK_STATUS status;
	private String taskResultSummary = "";

	
	public CompareImpl(CustomTaskTemplate template) {
		super(template);
	}


	@Override
	public TASK_STATUS execute() {
		try {
			final CompareInputValue inputValue = (CompareInputValue)getInputValue();

			String source = inputValue.paramSource().toString();
			String target = inputValue.paramTarget().toString();

			// detect task status
			boolean isEqual = source.equals(target);
			taskResultSummary = (isEqual ? "equal" : "not equal");
			if (inputValue.paramSuccessOnEqual()) {
				return (isEqual ? TASK_STATUS.success : TASK_STATUS.failure);
			} else {
				return (!isEqual ? TASK_STATUS.success : TASK_STATUS.failure);
			}
		} catch (Exception e) {
			status = TASK_STATUS.failure;
			taskResultSummary = e.getLocalizedMessage();
			LOG.error(e);
		}
		return status;
	}

	
	
	@Override
	public TaskInputValue inputValueInstance() throws JSONException {
		return new CompareInputValue(this);
	}

	@Override
	public ITaskOutputValue outputValue() {
		return new ITaskOutputValue() {

			@Override
			public String getValue(String key) {
				return null;
			}
		};
	}


	@Override
	public String resultSummary() {
		return taskResultSummary;
	}

	@Override
	public String resultDetails() {
		return "N/A";
	}

}
