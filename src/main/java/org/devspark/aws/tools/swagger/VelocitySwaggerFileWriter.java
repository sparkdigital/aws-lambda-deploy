package org.devspark.aws.tools.swagger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.devspark.aws.tools.model.resources.EndpointResource;

public class VelocitySwaggerFileWriter implements SwaggerFileWriter{

	private static final String FILE_PATH = "d:\\swagger-file.json";
	private static final String TEMPLATE_PATH = "./src/main/resources/api-gateway-swagger-template.vm";
	
	@SuppressWarnings("resource")
	@Override
	public void createSwaggerFile(List<EndpointResource> resources) {
		
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.init();

		Template template = velocityEngine.getTemplate(TEMPLATE_PATH);

		VelocityContext context = new VelocityContext();

		//TODO: get info from project
		context.put("createdOn", new Date());
		context.put("apiName", "ThisIsTheAPIName");
		context.put("host", "mockHost");
		context.put("stage", "This/is/the/basepath");

		context.put("resources", EndpointResourceVelocityMapper.toVelocityContext(resources));

		StringWriter writer = new StringWriter();

		// Process the template and write the o/p to stream
		template.merge(context, writer);
				
		File file=new File(FILE_PATH);  
		try {
	        file.createNewFile();  
	        FileWriter fileWriter = new FileWriter(file);
	        String swaggerString = writer.toString();
			fileWriter.write(swaggerString);
			//TODO: write swagger file into a file.
			System.out.println(swaggerString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}

}
