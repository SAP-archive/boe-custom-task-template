package com.sap.afw.task.webi;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.crystaldecisions.sdk.plugin.authentication.enterprise.IsecEnterpriseBase;
import com.sap.bong.common.coretask.base.TaskParamDefs;
import com.sap.bong.common.coretask.base.TaskParamValues;
import com.sap.bong.common.coretask.base.TaskImplCore.TASK_STATUS;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskParamValues;
import com.sap.bong.task.custom.sdk.CustomTaskPluginActivator;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class WebiGetDatasetImplTest {

	private CustomTaskTemplate taskTemplate;
	private CustomTaskImpl taskImpl;
	
	private ISessionMgr sessionMgr = null;
    private IEnterpriseSession eSession = null;
    
    private static String USER = "administrator";
    private static String PASSWORD = "Password1";
    private static String CMS = "localhost";

    private static final String CRLF = "\r\n";
    
    private String parWebiDoc = "";
    private String parReportTable = "";

	@Before
	/**
	 * Mockup the Automation Framework and create a BOE session
	 * @throws Exception
	 */
	public void setUp() throws Exception {
		// start the task template plugin
		this.taskTemplate = new WebiGetDatasetTemplate();
		Bundle bundle = FrameworkUtil.getBundle(taskTemplate.getClass());
		bundle.start(Bundle.START_TRANSIENT);

		// some basic checks for interface implementation
		assertNotNull("missing implementation of getTaskImpl()", taskTemplate.getTaskImpl());
		assertNotNull("missing implementation of getTaskTemplateJSONFilename()", taskTemplate.getTaskTemplateJSONFilename());
		
		// read and set the task template definition from JSON
		JSONObject taskTemplateJSON = CustomTaskPluginActivator.INSTANCE.readTaskTemplateJSON(taskTemplate.getTaskTemplateJSONFilename());
		assertNotNull("task template JSON", taskTemplateJSON);
		taskTemplate.setParamDefs(new TaskParamDefs(taskTemplateJSON));

		// create an instance of the task and set input values for execution
		taskImpl = new WebiGetDatasetImpl(taskTemplate);
//		taskImpl.setParamValues(getTaskParamValues());
//		assertEquals("task CUID", taskTemplate.getParamDefs().getCUID(), taskImpl.getParamValues().getCUID());
		
		// create a BOE session
		sessionMgr = CrystalEnterprise.getSessionMgr();
		eSession = sessionMgr.logon(USER, PASSWORD, CMS, IsecEnterpriseBase.PROGID);
		taskImpl.setSerializedSession(eSession.getSerializedSession());
	}
		
	@After
	public void tearDown() throws Exception {
		eSession.logoff();
	}

	/**
	 * Provide the input and output parameters as JSON Array
	 * @return
	 */
	private CustomTaskParamValues getTaskParamValues() {
		// basic parameter for the task execution
		String taskParam = "{'cuid': '" + taskTemplate.getParamDefs().getCUID() + "', 'name': '" + taskTemplate.getParamDefs().getName() + "',";
		// set your input and output parameters - should match your JSON definition of the task template (e.g. sampleTaskTemplate.json)
		taskParam = taskParam + 
				"'input_param': {'webi_doc': " + parWebiDoc + ", 'report_table': " + parReportTable + "}," +
				"'output_param': {'sample_output_param': ''}}";
		return new CustomTaskParamValues(new TaskParamValues(new JSONObject(taskParam)));
	}
	
	
	@Test
	/**
	 * Start your task template execution
	 * @throws Exception
	 */
	public void test() throws Exception {
		parWebiDoc = "[{cuid:'AQtkbbSqN4NOj3ydf.Sw1lY'}]";  // ZZ_Multiple Data Sources
		parReportTable = "[{report: 'Tables', table: 'Table2'}]";
		taskImpl.setParamValues(getTaskParamValues());
		taskImpl.setInputValue(new WebiGetDatasetInputValue(taskImpl));

		TASK_STATUS exec = taskImpl.execute();
		assertEquals("Task execution", TASK_STATUS.success, exec);
		assertEquals("result summary", "processed Tables.Table2 rows:3", taskImpl.resultSummary());
	}


}
