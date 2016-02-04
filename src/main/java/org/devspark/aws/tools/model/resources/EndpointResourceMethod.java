package org.devspark.aws.tools.model.resources;

import java.util.List;

public class EndpointResourceMethod {
	
	private String method;
	private List<String> produces;
	private List<String> parameters;
	private List<EndpointResponse> responses;
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public List<String> getProduces() {
		return produces;
	}
	public void setProduces(List<String> produces) {
		this.produces = produces;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public List<EndpointResponse> getResponses() {
		return responses;
	}
	public void setResponses(List<EndpointResponse> responses) {
		this.responses = responses;
	}
}
