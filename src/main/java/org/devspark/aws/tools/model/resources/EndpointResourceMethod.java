package org.devspark.aws.tools.model.resources;

import java.util.List;

public class EndpointResourceMethod {

	private String verb;
	private List<String> produces;
	private List<EndpointResourceMethodParameter> parameters;
	private List<EndpointResponse> responses;

	public List<String> getProduces() {
		return produces;
	}

	public void setProduces(List<String> produces) {
		this.produces = produces;
	}

	public List<EndpointResponse> getResponses() {
		return responses;
	}

	public void setResponses(List<EndpointResponse> responses) {
		this.responses = responses;
	}

	public List<EndpointResourceMethodParameter> getParameters() {
		return parameters;
	}

	public void setParameters(
			List<EndpointResourceMethodParameter> parameters) {
		this.parameters = parameters;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

}
