package org.devspark.aws.tools;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class MockSwaggerFileGenerator {

	public static void main(String[] args) throws Exception {

		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		Template template = velocityEngine.getTemplate(
				"./src/main/resources/api-gateway-swagger-template.vm");

		VelocityContextMock velocityContextMock = new VelocityContextMock();

		// Get the Velocity Context object.
		VelocityContext context = velocityContextMock.getMockContent();

		StringWriter writer = new StringWriter();

		// Process the template and write the o/p to stream
		template.merge(context, writer);

		System.out.println(writer);
	}

}
