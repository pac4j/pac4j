package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Builds metadata and the relevant resolvers.
 * @author Misagh Moayyed
 */
public interface SAMLMetadataGenerator {
    MetadataResolver buildMetadataResolver(Resource metadataResource) throws IOException, MarshallingException, ComponentInitializationException, ResolverException;

    String getMetadata(EntityDescriptor entityDescriptor) throws MarshallingException;

    EntityDescriptor buildEntityDescriptor();
}
