package org.devspark.aws.tools.model.resources;

import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.devspark.aws.tools.swagger.EndpointResourceVelocityMapper;
import org.junit.Test;

public class EndpointResourceVelocityMapperTest {

	@Test
	public void test() {
		List<Map<String, Object>> list = EndpointResourceVelocityMapper
				.toVelocityContext(mockResourceList());
		assertNotNull(list);

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		Template template = velocityEngine.getTemplate(
				"./src/main/resources/api-gateway-swagger-template.vm");

		VelocityContext context = new VelocityContext();

		context.put("createdOn", new Date());
		context.put("apiName", "ThisIsTheAPIName");
		context.put("host", "mockHost");
		context.put("stage", "This/is/the/basepath");

		context.put("resources", list);

		StringWriter writer = new StringWriter();

		// Process the template and write the o/p to stream
		template.merge(context, writer);

		System.out.println(writer);

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
		EndpointResourceMethod method = new EndpointResourceMethod();
		method.setVerb("GET");
		EndpointResourceMethodParameter parameter = new EndpointResourceMethodParameter();
		parameter.setName("id");
		parameter.setIn("path");
		parameter.setRequired(true);
		parameter.setType("string");
		List<EndpointResourceMethodParameter> parameters = new ArrayList<EndpointResourceMethodParameter>();
		parameters.add(parameter);
		method.setParameters(parameters);
		List<String> produces = new ArrayList<String>();
		produces.add("application/json");
		method.setProduces(produces);
		List<EndpointResponse> responses = new ArrayList<EndpointResponse>();
		responses.add(response);
		method.setResponses(responses);

		List<EndpointResourceMethod> methods = new ArrayList<EndpointResourceMethod>();
		methods.add(method);
		resource.setMethods(methods);

		List<EndpointResource> resources = new ArrayList<EndpointResource>();
		resources.add(resource);
		return resources;
	}
}
