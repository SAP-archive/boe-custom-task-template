package com.sap.afw.task.compare;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.sap.bong.common.coretask.base.TaskParamDefs;
import com.sap.bong.common.coretask.base.TaskParamValues;
import com.sap.bong.common.coretask.base.TaskImplCore.TASK_STATUS;
import com.sap.bong.task.custom.sdk.CustomTaskImpl;
import com.sap.bong.task.custom.sdk.CustomTaskParamValues;
import com.sap.bong.task.custom.sdk.CustomTaskPluginActivator;
import com.sap.bong.task.custom.sdk.CustomTaskTemplate;

public class CompareImplTest {

	private CustomTaskTemplate taskTemplate;
	private CustomTaskImpl taskImpl;
	
    private static final String CRLF = "\r\n";
    
    private String parSource = "";
    private String parTarget = "";
    private String parCompare = "";

	@Before
	/**
	 * Mockup the Automation Framework and create a BOE session
	 * @throws Exception
	 */
	public void setUp() throws Exception {
		// start the task template plugin
		this.taskTemplate = new CompareTemplate();
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
		taskImpl = new CompareImpl(taskTemplate);
	}
		
	@After
	public void tearDown() throws Exception {
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
				"'input_param': {'source': '" + parSource + "', 'target': '" + parTarget + "', 'compare_condition': '" + parCompare + "'}," +
				"'output_param': {'csv_output': ''}}";
		return new CustomTaskParamValues(new TaskParamValues(new JSONObject(taskParam)));
	}
	
	
	@Test
	/**
	 * Start your task template execution
	 * @throws Exception
	 */
	public void test() throws Exception {
		parSource = "cuid;abc;xyz";  // header: cuid, values: abc xyz
		parTarget = "cuid;abc;xyz";
		parCompare = "equal";
		
		taskImpl.setParamValues(getTaskParamValues());
		taskImpl.setInputValue(new CompareInputValue(taskImpl));
		TASK_STATUS exec = taskImpl.execute();
		assertEquals("Task execution", TASK_STATUS.success, exec);
		assertEquals("result summary", "equal", taskImpl.resultSummary());

		parCompare = "not_equal";
		taskImpl.setParamValues(getTaskParamValues());
		taskImpl.setInputValue(new CompareInputValue(taskImpl));
		exec = taskImpl.execute();
		assertEquals("Task execution", TASK_STATUS.failure, exec);
		assertEquals("result summary", "equal", taskImpl.resultSummary());

		parTarget = "cuid;xyz";
		parCompare = "equal";
		taskImpl.setParamValues(getTaskParamValues());
		taskImpl.setInputValue(new CompareInputValue(taskImpl));
		exec = taskImpl.execute();
		assertEquals("Task execution", TASK_STATUS.failure, exec);
		assertEquals("result summary", "not equal", taskImpl.resultSummary());

		parCompare = "not equal";
		taskImpl.setParamValues(getTaskParamValues());
		taskImpl.setInputValue(new CompareInputValue(taskImpl));
		exec = taskImpl.execute();
		assertEquals("Task execution", TASK_STATUS.success, exec);
		assertEquals("result summary", "not equal", taskImpl.resultSummary());
	}
}
