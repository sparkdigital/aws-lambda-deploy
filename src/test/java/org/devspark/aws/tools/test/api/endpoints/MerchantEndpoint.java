package org.devspark.aws.tools.test.api.endpoints;

import org.devspark.aws.lambdasupport.endpoint.BaseRepositoryEndpoint;
import org.devspark.aws.lambdasupport.endpoint.Configuration;
import org.devspark.aws.lambdasupport.endpoint.annotations.apigateway.Resource;
import org.devspark.aws.lambdasupport.test.api.model.Merchant;
import org.devspark.aws.lorm.EntityManager;
import org.devspark.aws.lorm.Repository;
import org.devspark.aws.lorm.mapping.EntityToItemMapperImpl;
import org.devspark.aws.lorm.mapping.ItemToEntityMapperImpl;

@Resource(name="merchant")
public class MerchantEndpoint extends BaseRepositoryEndpoint<Merchant> {

	private final Repository<Merchant> merchantRepository;

	public MerchantEndpoint() {
		EntityManager entityManager = Configuration.getEntitymanager();
		entityManager.addEntity(Merchant.class,
				new EntityToItemMapperImpl<Merchant>(Merchant.class),
				new ItemToEntityMapperImpl<Merchant>(Merchant.class,
						entityManager), new EntityToItemMapperImpl<Merchant>(
						Merchant.class));

		merchantRepository = entityManager.getRepository(Merchant.class);
	}
	
	@Override
	protected Repository<Merchant> getRepository() {
		return merchantRepository;
	}

}
