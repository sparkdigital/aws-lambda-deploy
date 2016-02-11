package org.devspark.aws.tools.swagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.devspark.aws.tools.model.resources.EndpointResource;
import org.devspark.aws.tools.model.resources.EndpointResourceMethod;
import org.devspark.aws.tools.model.resources.EndpointResponse;

public class EndpointResourceVelocityMapper {
	
	public static final List<Map<String, Object>> toVelocityContext(List<EndpointResource> resources) {

		List<Map<String, Object>> resourceList = new ArrayList<Map<String,Object>>();
		for(EndpointResource resource: resources) {
			Map<String, Object> resourcesMap = new HashMap<String, Object>();
			resourcesMap.put("name", resource.getName());
			resourcesMap.put("methods", mapEndpointMethods(resource.getMethods()));
			resourceList.add(resourcesMap);
		}
		return resourceList;
		
	}

	private static List<Object> mapEndpointMethods(List<EndpointResourceMethod> methods) {
		List<Object> methodsList = new ArrayList<Object>();
		for(EndpointResourceMethod method: methods) {
			Map<String, Object> methodDetailsMap = new HashMap<String, Object>();
			methodDetailsMap.put("produces", method.getProduces());
			methodDetailsMap.put("parameters", method.getParameters());
			methodDetailsMap.put("responses", mapEndpointResponses(method.getResponses()));
			methodDetailsMap.put("verb", method.getVerb().toLowerCase());
			methodsList.add(methodDetailsMap);
		}
		return methodsList;
	}

	private static List<Map<String, Object>> mapEndpointResponses(List<EndpointResponse> responses) {
		List<Map<String, Object>> responseMapList = new ArrayList<Map<String, Object>>();
		for(EndpointResponse response : responses) {
			Map<String, Object> responseMap = new HashMap<String, Object>();
			Map<String, Object> responseDetailsMap = new HashMap<String, Object>();
			responseDetailsMap.put("description", response.getDescription());
			Map<String, String> schemaMap = new HashMap<String, String>();
			schemaMap.put("$ref", response.getSchema().getRef());
			responseDetailsMap.put("schema", schemaMap);
			responseDetailsMap.put("headers", new HashMap<String, String>());
			responseMap.put(response.getHttpStatus(), responseDetailsMap);
			responseMapList.add(responseMap);
		}
		return responseMapList;
	}

}
