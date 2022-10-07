package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Builds metadata and the relevant resolvers.
 * @author Misagh Moayyed
 */
public interface SAML2MetadataGenerator {
    MetadataResolver buildMetadataResolver() throws Exception;

    String getMetadata(EntityDescriptor entityDescriptor) throws Exception;

    EntityDescriptor buildEntityDescriptor();

    boolean storeMetadata(String metadata, boolean force) throws Exception;
}
