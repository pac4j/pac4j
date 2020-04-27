package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.Resource;

/**
 * Builds metadata and the relevant resolvers.
 * @author Misagh Moayyed
 */
public interface SAMLMetadataGenerator {
    MetadataResolver buildMetadataResolver(Resource metadataResource) throws Exception;

    String getMetadata(EntityDescriptor entityDescriptor) throws Exception;

    EntityDescriptor buildEntityDescriptor();

    boolean storeMetadata(String metadata, Resource resource, boolean force) throws Exception;
}
