package org.devspark.aws.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.devspark.aws.lambdasupport.endpoint.annotations.lambda.Lambda;
import org.devspark.aws.lambdasupport.endpoint.annotations.lambda.LambdaHandler;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeResult;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationResult;

@Mojo(name = "lambda-deployer")
public class AWSLambdaDeployer extends AbstractMojo {
	private String basePackage;

	/**
	 * The amount of memory that will be used by the AWS Lambda service to
	 * execute the function. AWS Lambda uses this memory size to infer the
	 * amount of CPU allocated to your function. The default is 512 (MB)
	 */
	@Parameter(property = "lambda.memory", defaultValue = "512")
	private int memory;

	/**
	 * The function execution time at which AWS Lambda should terminate the
	 * function. Because the execution time has cost implications, we recommend
	 * you set this value based on your expected execution time. The default is
	 * 5000 (milliseconds).
	 */
	@Parameter(property = "lambda.timeout", defaultValue = "5000")
	private int handlerTimeout;
	
	/**
	 * The S3 bucket name where the lambda code is stored.
	 */
	private String codeS3Bucket;
	@Parameter(property = "lambda.code.s3-bucket-name")
	
	// TODO these ones should be determined at runtime
	private String artifactName;
	private String codeS3Version;
	
	@Parameter(property = "lambda.timeout", defaultValue = "5000")
	private String lambdaRoleARN;
	

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forPackage(basePackage)).setScanners(
						new SubTypesScanner()));
		Set<Class<?>> lambdaTypes = reflections
				.getTypesAnnotatedWith(Lambda.class);

		AWSLambdaClient lambdaClient = new AWSLambdaClient();
		for (Class<?> type : lambdaTypes) {
			Set<Method> lambdaHandlers = ReflectionUtils.getAllMethods(type,
					ReflectionUtils.withAnnotation(LambdaHandler.class));

			if (lambdaHandlers.isEmpty()) {
				getLog().warn(
						"No methods annotated with @LambdaHandler found in type: "
								+ type.getName());
				continue;
			}

			if (lambdaHandlers.size() > 1) {
				getLog().warn(
						"More than one method annotated with @LambdaHandler found in type: "
								+ type.getName());
				continue;
			}

			String handlerMethod = type.getName() + ":"
					+ lambdaHandlers.iterator().next().getName();
			Lambda lambdaData = type.getAnnotation(Lambda.class);

			GetFunctionRequest getFunctionReq = new GetFunctionRequest();
			GetFunctionResult getFunctionRes = lambdaClient
					.getFunction(getFunctionReq);
			if (getFunctionRes != null) {
				if (needsConfigurationUpdate(getFunctionRes, lambdaData.name(),
						handlerMethod)) {
					updateLambdaConfiguration(lambdaClient, lambdaData.name(),
							lambdaData.description(), handlerMethod);
				} else if (needsCodeUpdate(getFunctionRes)) {
					updateLambdaCode(lambdaClient, lambdaData.name());
				} else {
					getLog().warn(
							"Configuration and code is up to date, nothing to change.");
				}
			} else {
				createLambda(lambdaClient, handlerMethod, lambdaData.name(),
						lambdaData.description());
			}

		}

	}

	private boolean needsCodeUpdate(GetFunctionResult getFunctionRes) {
		String codeHash = getFunctionRes.getConfiguration().getCodeSha256();

		boolean needsUpdate = true;
		FileInputStream in = null;
		try {
			in = new FileInputStream(new File(artifactName));
			String hash = DigestUtils.sha256Hex(in);

			needsUpdate = !codeHash.equals(hash);
		} catch (IOException ex) {
			getLog().warn(
					"Error calculating hash of function code. It will be redeployed");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
				}
			}
		}

		return needsUpdate;
	}

	private boolean needsConfigurationUpdate(GetFunctionResult getFunctionRes,
			String functionName, String handlerMethod) {
		boolean needsReDeploy = true;
		FunctionConfiguration conf = getFunctionRes.getConfiguration();

		if (conf.getHandler().equals(handlerMethod)) {
			if (conf.getMemorySize().equals(memory)) {
				if (conf.getRole().equals(lambdaRoleARN)) {
					if (conf.getTimeout().equals(handlerTimeout)) {
						needsReDeploy = false;
					} else {
						warnReDeploy(functionName, "timeout",
								String.valueOf(conf.getTimeout()),
								String.valueOf(handlerTimeout));
					}
				} else {
					warnReDeploy(functionName, "role", conf.getRole(),
							lambdaRoleARN);
				}
			} else {
				warnReDeploy(functionName, "memory",
						String.valueOf(conf.getMemorySize()),
						String.valueOf(memory));
			}
		} else {
			warnReDeploy(functionName, "handler method", conf.getHandler(),
					handlerMethod);
		}

		return needsReDeploy;
	}

	private void warnReDeploy(String functionName, String attr,
			String oldConfVal, String newConfVal) {
		getLog().warn(
				"Function ["
						+ functionName
						+ "] will be redeployed as configuration needs to be changed. Attribute: ["
						+ attr + "] " + ", previous value: [" + oldConfVal
						+ "], new value [" + newConfVal + "]");
	}

	private FunctionCode buildFunctionCode() {
		FunctionCode functionCode = new FunctionCode();
		functionCode.setS3Bucket(codeS3Bucket);
		functionCode.setS3Key(artifactName);
		functionCode.setS3ObjectVersion(codeS3Version);

		return functionCode;
	}

	private String createLambda(AWSLambdaClient lambdaClient, String handler,
			String name, String description) {
		CreateFunctionRequest request = new CreateFunctionRequest();
		request.setCode(buildFunctionCode());
		request.setDescription(description);
		request.setFunctionName(name);
		request.setHandler(handler);
		request.setMemorySize(memory);
		request.setPublish(true);
		// assume or create a role?
		request.setRole(lambdaRoleARN);
		request.setRuntime("java8");
		request.setTimeout(handlerTimeout);

		CreateFunctionResult result = lambdaClient.createFunction(request);

		return result.getFunctionArn();
	}

	private String updateLambdaCode(AWSLambdaClient lambdaClient,
			String functionName) {
		UpdateFunctionCodeRequest request = new UpdateFunctionCodeRequest();
		request.setFunctionName(functionName);
		request.setPublish(true);
		request.setS3Bucket(codeS3Bucket);
		request.setS3Key(artifactName);
		request.setS3ObjectVersion(codeS3Version);

		UpdateFunctionCodeResult result = lambdaClient
				.updateFunctionCode(request);

		return result.getFunctionArn();
	}

	private String updateLambdaConfiguration(AWSLambdaClient lambdaClient,
			String functionName, String description, String handlerMethod) {

		UpdateFunctionConfigurationRequest request = new UpdateFunctionConfigurationRequest();
		request.setDescription(description);
		request.setFunctionName(functionName);
		request.setHandler(handlerMethod);
		request.setMemorySize(memory);
		request.setRole(lambdaRoleARN);
		request.setTimeout(handlerTimeout);

		UpdateFunctionConfigurationResult result = lambdaClient
				.updateFunctionConfiguration(request);

		return result.getFunctionArn();
	}
}
