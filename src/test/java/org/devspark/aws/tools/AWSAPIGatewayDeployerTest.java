package org.devspark.aws.tools;

import java.lang.reflect.Field;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

public class AWSAPIGatewayDeployerTest {
	
	
	private AWSAPIGatewayDeployer gatewayDeployer = new AWSAPIGatewayDeployer();

	@Before
	public void setup() {
		Field field;
		try {
			field = AWSAPIGatewayDeployer.class.getDeclaredField("basePackage");
			field.setAccessible(true);
			field.set(gatewayDeployer, "org.devspark.aws.tools.test.api.endpoints");
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void test() {
		try {
			gatewayDeployer.execute();
		} catch (MojoExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MojoFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
