package org.devspark.aws.tools.test.api;

import java.util.Arrays;
import java.util.List;

import org.devspark.aws.lambdasupport.endpoint.Endpoint;
import org.devspark.aws.lambdasupport.endpoint.GenericHandler;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.ApiGateway;
import org.devspark.aws.lambdasupport.endpoint.annotations.lambda.Lambda;
import org.devspark.aws.tools.test.api.endpoints.MerchantEndpoint;

@Lambda(name="expense-api")
@ApiGateway(name="expense")
public class Handler extends GenericHandler {

	@Override
	protected List<Endpoint> getEndpoints() {
		return Arrays.asList(new Endpoint[] { new MerchantEndpoint() });
	}

}
