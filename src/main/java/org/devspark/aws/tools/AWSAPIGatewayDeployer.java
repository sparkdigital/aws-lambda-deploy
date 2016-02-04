package org.devspark.aws.tools;

import java.lang.reflect.Method;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.Resource;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.ResourceMethod;
import org.devspark.aws.lambdasupport.endpoint.annotations.lambda.LambdaHandler;
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
		
		for (Class<?> type : resources) {
			Set<Method> resourceMethods = ReflectionUtils.getAllMethods(type,
					ReflectionUtils.withAnnotation(ResourceMethod.class));

			if (resourceMethods.isEmpty()) {
				getLog().warn(
						"No methods annotated with @Resource found in type: "
								+ type.getName());
				continue;
			}
		}

	}

}
