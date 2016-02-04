package org.devspark.aws.tools.model.resources;

public class EndpointResponse {
	
	private String httpStatus = "200";
	private String description;
	private EndpointResponseSchema schema;
	private EndpointResponseHeader headers;
	
	public String getHttpStatus() {
		return httpStatus;
	}
	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public EndpointResponseSchema getSchema() {
		return schema;
	}
	public void setSchema(EndpointResponseSchema schema) {
		this.schema = schema;
	}
	public EndpointResponseHeader getHeaders() {
		return headers;
	}
	public void setHeaders(EndpointResponseHeader headers) {
		this.headers = headers;
	}

}
