package org.devspark.aws.tools.model.resources;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class EndpointResourceVelocityMapperTest {

	
	@Test
	public void test() {
		Map<String, Object> map = EndpointResourceVelocityMapper.toVelocityContext(mockResourceList());
		assertNotNull(map);
	}
	
	private List<EndpointResource> mockResourceList() {
		EndpointResponse response = new EndpointResponse();
		response.setDescription("200 response");
		response.setHttpStatus("200");
		response.setHeaders(new EndpointResponseHeader());
		EndpointResponseSchema schema = new EndpointResponseSchema();
		schema.setRef("#/definitions/Empty");
		response.setSchema(schema);
		
		EndpointResource resource = new EndpointResource();
		resource.setName("pets");
		EndpointResourceMethod getPet = new EndpointResourceMethod();
		getPet.setMethod("GET");
		getPet.setParameters(new ArrayList<String>());
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		getPet.setProduces(produces);
		List<EndpointResponse> responses = new ArrayList<EndpointResponse>();
		responses.add(response);
		getPet.setResponses(responses);
		
		List<EndpointResourceMethod> methods = new ArrayList<EndpointResourceMethod>();
		methods.add(getPet);
		resource.setMethods(methods);
		
		List<EndpointResource> resources = new ArrayList<EndpointResource>();
		resources.add(resource);
		return resources;
	}
}
