package org.devspark.aws.tools.swagger;

import java.util.List;

import org.devspark.aws.tools.model.resources.EndpointResource;

public interface SwaggerFileWriter {

	public void createSwaggerFile(List<EndpointResource> resources);

}
