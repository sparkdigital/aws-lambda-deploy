package org.devspark.aws.tools;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.Resource;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.ResourceMethod;
import org.devspark.aws.tools.model.resources.EndpointResource;
import org.devspark.aws.tools.model.resources.EndpointResourceMethod;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

@Mojo(name = "apigateway-deployer")
public class AWSAPIGatewayDeployer extends AbstractMojo {

	@Parameter(property = "base-package")
	private String basePackage;

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(basePackage)).setScanners(
						new SubTypesScanner()));

		Set<Class<?>> resources = reflections
				.getTypesAnnotatedWith(Resource.class);
		
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
				String resourceName = "/" + type.getName();
				if(methodResource != null) {
					resourceName = resourceName + "/" + methodResource.name();
				}
				EndpointResourceMethod endpointMethod = createMethodResource(method, resourceName);
				EndpointResource endpointResource = Optional.ofNullable(endpointResources.get(resourceName))
						.orElse(new EndpointResource());
				if( endpointResource.getMethods() == null) {
					endpointResource.setMethods(new ArrayList<EndpointResourceMethod>());
				}
				endpointResource.getMethods().add(endpointMethod);
			}
			
			
		}

	}

	private EndpointResourceMethod createMethodResource(Method method, String resourceName) {
		EndpointResourceMethod endpointMethod = new EndpointResourceMethod();
		ResourceMethod resourceMethod = method.getAnnotation(ResourceMethod.class);
		endpointMethod.setMethod(resourceMethod.httpMethod().name());
		endpointMethod.setParameters(getParameters(resourceName));
		endpointMethod.setProduces(Arrays.asList("application/json"));
		//endpointMethod.setResponses(responses);
		return null;
	}

	private List<String> getParameters(String resourceName) {
		String pattern = "\\{[a-zA-A]*\\}";
		Pattern r = Pattern.compile(pattern);

		List<String> parameters = new ArrayList<String>();
		Matcher m = r.matcher(resourceName);
		while(m.find()){			
			String parameter = m.group(0);
			parameters.add(parameter.replaceAll("\\{*\\}*", ""));
	    }
		return parameters;
	}

}
