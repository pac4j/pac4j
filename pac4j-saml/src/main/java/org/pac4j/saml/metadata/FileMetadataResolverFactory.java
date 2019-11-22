package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.springframework.core.io.Resource;

public class FileMetadataResolverFactory implements MetadataResolverFactory {

	@Override
	public AbstractBatchMetadataResolver getInstance(Resource metadataResource) throws Exception {
		return new FilesystemMetadataResolver(metadataResource.getFile());
	}

}
