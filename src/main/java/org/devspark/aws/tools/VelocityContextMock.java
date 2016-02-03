package org.devspark.aws.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.VelocityContext;

public class VelocityContextMock {

	/**
	 * Populates parameters.
	 * 
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> setMockParameters() {

		List<Map<String, Object>> parametersList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 5; i++) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			parameterMap.put("name", "parameterName" + i);
			parameterMap.put("required", true);
			parametersList.add(parameterMap);
		}

		return parametersList;
	}

	/**
	 * Populates methods.
	 * 
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> setMockMethods() {

		List<Map<String, Object>> methodsList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 5; i++) {
			Map<String, Object> methodMap = new HashMap<String, Object>();
			methodMap.put("name", "methodName" + i);
			methodMap.put("parameters", setMockParameters());
			methodsList.add(methodMap);
		}

		return methodsList;
	}

	/**
	 * Populates resources.
	 * 
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> setMockResources() {

		List<Map<String, Object>> resourcesList = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 5; i++) {
			Map<String, Object> resourceMap = new HashMap<String, Object>();
			resourceMap.put("name", "resourceName" + i);
			resourceMap.put("methods", setMockMethods());
			resourcesList.add(resourceMap);
		}
		return resourcesList;
	}

	public VelocityContext getMockContent() {
		VelocityContext context = new VelocityContext();

		context.put("createdOn", new Date());
		context.put("apiName", "ThisIsTheAPIName");
		context.put("host", "mockHost");
		context.put("stage", "This/is/the/basepath");

		context.put("resources", setMockResources());
		return context;
	}

}
