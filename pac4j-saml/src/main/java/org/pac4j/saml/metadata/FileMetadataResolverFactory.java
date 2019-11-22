package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.springframework.core.io.Resource;

public class FileMetadataResolverFactory implements SAML2MetadataResolverFactory {

    @Override
    public MetadataResolver getInstance(Resource metadataResource) throws Exception {
        return new FilesystemMetadataResolver(metadataResource.getFile());
    }

}
