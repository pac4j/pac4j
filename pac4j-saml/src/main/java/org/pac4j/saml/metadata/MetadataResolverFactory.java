package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.springframework.core.io.Resource;

public interface MetadataResolverFactory {

	AbstractBatchMetadataResolver getInstance(Resource metadataResource) throws Exception;

}
