package org.devspark.aws.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.ApiGateway;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.Resource;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.ResourceMethod;
import org.devspark.aws.tools.model.resources.EndpointResource;
import org.devspark.aws.tools.model.resources.EndpointResourceMethod;
import org.devspark.aws.tools.model.resources.EndpointResourceMethodParameter;
import org.devspark.aws.tools.model.resources.EndpointResponse;
import org.devspark.aws.tools.model.resources.EndpointResponseHeader;
import org.devspark.aws.tools.model.resources.EndpointResponseSchema;
import org.devspark.aws.tools.swagger.SwaggerFileWriter;
import org.devspark.aws.tools.swagger.VelocitySwaggerFileWriter;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@Mojo(name = "apigateway-deployer")
public class AWSAPIGatewayDeployer extends AbstractMojo {

	@Parameter(property = "base-package")
	private String basePackage;
	
	private SwaggerFileWriter fileWriter = new VelocitySwaggerFileWriter();

	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(basePackage)).setScanners(
						new SubTypesScanner(), new TypeAnnotationsScanner()));

		Set<Class<?>> resources = reflections
				.getTypesAnnotatedWith(Resource.class);
		
		Set<Class<?>> apis = reflections
				.getTypesAnnotatedWith(ApiGateway.class);
		
		Map<String, EndpointResource> endpointResources = getEndpointResources(resources);
		String apiName = getApiName(apis);
		fileWriter.createSwaggerFile(new ArrayList<EndpointResource>(endpointResources.values()), apiName);

	}
	
	private String getApiName(Set<Class<?>> apis) {
		if (apis.size() != 1) {
			getLog().warn("Invalid number of @ApiGateway found.");
		}
		return apis.iterator().next().getAnnotationsByType(ApiGateway.class)[0].name();
	}

	@SuppressWarnings("unchecked")
	private Map<String, EndpointResource> getEndpointResources(Set<Class<?>> resources) {
		Map<String, EndpointResource> endpointResources = new HashMap<String, EndpointResource>();
		
		for (Class<?> type : resources) {
			Set<Method> resourceMethods = ReflectionUtils.getAllMethods(type,
					ReflectionUtils.withAnnotation(ResourceMethod.class));

			if (resourceMethods.isEmpty()) {
				getLog().warn(
						"No methods annotated with @Resource found in type: "
								+ type.getName());
				continue;
			}
			
			for (Method method : resourceMethods) {
				Resource methodResource = method.getAnnotation(Resource.class);
				String resourceName = type.getAnnotationsByType(Resource.class)[0].name();
				if(methodResource != null) {
					resourceName = resourceName + "/" + methodResource.name();
				}
				EndpointResourceMethod endpointMethod = createMethodResource(method, resourceName);
				EndpointResource endpointResource = endpointResources.get(resourceName);
				if (endpointResource == null) {
					endpointResource = new EndpointResource();
					endpointResource.setName(resourceName);
					endpointResource.setMethods(new ArrayList<EndpointResourceMethod>());
					endpointResources.put(resourceName, endpointResource);
				}
				endpointResource.getMethods().add(endpointMethod);
			}
		}
		return endpointResources;
	}

	private EndpointResourceMethod createMethodResource(Method method, String resourceName) {
		EndpointResourceMethod endpointMethod = new EndpointResourceMethod();
		ResourceMethod resourceMethod = method.getAnnotation(ResourceMethod.class);
		endpointMethod.setVerb(resourceMethod.httpMethod().name());
		endpointMethod.setParameters(getParameters(resourceName));
		endpointMethod.setProduces(Arrays.asList("application/json"));
		endpointMethod.setResponses(getMethodResponses());
		return endpointMethod;
	}

	//TODO: Replace mocked list with the generation of the responses of the method.
	private List<EndpointResponse> getMethodResponses() {
		List<EndpointResponse> responses = new ArrayList<EndpointResponse>();
		EndpointResponse sucessfulResponse = new EndpointResponse();
		sucessfulResponse.setHttpStatus("200");
		sucessfulResponse.setDescription("200 response");
		sucessfulResponse.setHeaders(new EndpointResponseHeader());
		EndpointResponseSchema schema = new EndpointResponseSchema();
		schema.setRef("#/definitions/Empty");
		sucessfulResponse.setSchema(schema);
		return responses;
	}

	private List<EndpointResourceMethodParameter> getParameters(String resourceName) {
		String pattern = "\\{[a-zA-A]*\\}";
		Pattern r = Pattern.compile(pattern);

		List<EndpointResourceMethodParameter> parameters = new ArrayList<EndpointResourceMethodParameter>();
		Matcher m = r.matcher(resourceName);
		while(m.find()){	
			EndpointResourceMethodParameter parameter = new EndpointResourceMethodParameter();
			parameter.setName(m.group(0).replaceAll("\\{*\\}*", ""));
			//TODO: Review how to populate the parameter metadata.
			parameter.setRequired(true);
			parameter.setType("string");
			parameter.setIn("path");
			parameters.add(parameter);
	    }
		return parameters;
	}

}
