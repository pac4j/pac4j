package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.springframework.core.io.Resource;

public interface SAML2MetadataResolverFactory {

    MetadataResolver getInstance(Resource metadataResource) throws Exception;

}
