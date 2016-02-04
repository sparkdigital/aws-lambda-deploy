package org.devspark.aws.tools.model.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndpointResourceVelocityMapper {
	
	public static final Map<String, Object> toVelocityContext(List<EndpointResource> resources) {

		Map<String, Object> resourcesMap = new HashMap<String, Object>();
		for(EndpointResource resource: resources) {
			resourcesMap.put("/" + resource.getName(), mapEndpointMethods(resource.getMethods()));
		}
		return resourcesMap;
		
	}

	private static Map<String, Object> mapEndpointMethods(List<EndpointResourceMethod> methods) {
		Map<String, Object> methodsMap = new HashMap<String, Object>();
		for(EndpointResourceMethod method: methods) {
			Map<String, Object> methodDetailsMap = new HashMap<String, Object>();
			methodDetailsMap.put("produces", method.getProduces());
			methodDetailsMap.put("parameters", method.getParameters());
			methodDetailsMap.put("responses", mapEndpointResponses(method.getResponses()));
			methodsMap.put(method.getVerb(), methodDetailsMap);
		}
		return methodsMap;
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
