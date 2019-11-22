package org.pac4j.saml.metadata;

import org.springframework.core.io.Resource;

public class FileMetadataResolverFactory implements SAML2MetadataResolverFactory {

    @Override
    public Pac4JMetadataResolver getInstance(Resource metadataResource) throws Exception {
        return new Pac4JFileSystemMetadataResolver(metadataResource);
    }

}
