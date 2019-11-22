package org.pac4j.saml.metadata;

import org.springframework.core.io.Resource;

public interface SAML2MetadataResolverFactory {

    Pac4JMetadataResolver getInstance(Resource metadataResource) throws Exception;

}
