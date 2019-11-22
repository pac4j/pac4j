package org.pac4j.saml.metadata;

import org.springframework.core.io.Resource;

public class FileMetadataResolverFactory implements SAML2MetadataResolverFactory {

    @Override
    public MetadataResolver getInstance(Resource metadataResource) throws Exception {
        return new Pac4jFileSystemMetadataResolver(metadataResource);
    }

}
