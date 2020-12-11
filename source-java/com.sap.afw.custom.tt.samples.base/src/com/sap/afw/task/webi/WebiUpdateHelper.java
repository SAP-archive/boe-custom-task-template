package com.sap.afw.task.webi;

import org.json.JSONArray;
import org.json.JSONObject;

import com.businessobjects.foundation.logging.ILogger;
import com.businessobjects.foundation.logging.LoggerManager;
import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.occa.infostore.IInfoObject;
import com.crystaldecisions.sdk.occa.infostore.IInfoObjects;
import com.sap.afw.helper.Framework;
import com.sap.bong.common.coretask.base.TaskImplCore.TASK_STATUS;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;

public class WebiUpdateHelper extends TaskHelper {

	private static final ILogger LOG = LoggerManager.getLogger(WebiUpdateHelper.class);
			
	private static final String CUID = "cuid";
	private static final String ID = "id";

	
	private WebiUpdateInputValue inputValue;
	private int docId;
	private String csvOutput;

	
	public WebiUpdateHelper(CustomTaskImpl taskImpl) throws Exception {
		super(taskImpl);
		inputValue = (WebiUpdateInputValue) taskImpl.getInputValue();
		csvOutput = "id" + Framework.COL_DELIMITER + "document" + Framework.ROW_DELIMITER_NEW_LINE;
	}

	public TASK_STATUS update() throws Exception {
		setResultSummary("processed Documents: 0");

		try {
			JSONArray parDocs = inputValue.paramDocuments();
			for (int i=0; i<parDocs.length(); i++) {
				JSONObject entry = parDocs.getJSONObject(i);
				// if entry has CUID, id will be ignored
				if (entry.has(CUID)) {
					String value = entry.getString(CUID);
					IInfoObjects infoObjects = getBIP().getInfoStore().query("select si_name, si_cuid, si_id from ci_infoObjects where si_cuid='" + entry.getString(CUID) + "'");
					docId = ((IInfoObject)infoObjects.get(0)).getID();
				} else if (entry.has(ID)) {
					docId = entry.getInt(ID);
				}
				// open and save the document
		        JSONObject document = getRest().sendRequestJSON(getRest().getWEBI_RWS() + "/documents/" + docId + "?overwrite=true", "GET", null);
		        String name = document.getJSONObject("document").getString("name");
		        document = getRest().sendRequestJSON(getRest().getWEBI_RWS() + "/documents/" + docId + "?overwrite=true", "POST", "{\"document\":{\"name\":\"" + name + "\",\"folderId\":-1}}");
		        csvOutput += docId + Framework.COL_DELIMITER + name + Framework.ROW_DELIMITER_NEW_LINE;
		        setResultSummary("processed Documents: " + (i + 1));
			}
	        return TASK_STATUS.success;
		} catch (Exception e) {
			LOG.error(e);
			return TASK_STATUS.failure;					
		}
	}

	public String getCsvOutput() {
		return csvOutput;
	}

}
