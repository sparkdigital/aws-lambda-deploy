package org.devspark.aws.tools.model.resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndpointResource {
	
	private String name;
	private List<EndpointResourceMethod> methods;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<EndpointResourceMethod> getMethods() {
		return methods;
	}
	public void setMethods(List<EndpointResourceMethod> methods) {
		this.methods = methods;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("name", name);
		map.put("methods", methods);
		return map;
	}
}
