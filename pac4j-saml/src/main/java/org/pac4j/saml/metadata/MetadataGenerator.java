package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Builds metadata and the relevant resolvers.
 * @author Misagh Moayyed
 */
public interface MetadataGenerator {
    MetadataResolver buildMetadataResolver() throws Exception;

    String getMetadata() throws Exception;

    EntityDescriptor buildEntityDescriptor();
}
