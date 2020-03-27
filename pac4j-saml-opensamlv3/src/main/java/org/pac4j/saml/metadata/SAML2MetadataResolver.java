package org.pac4j.saml.metadata;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;

import java.io.IOException;

/**
 * Defines operations required to resolve metadata for idp and sp.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2MetadataResolver {
    MetadataResolver resolve();

    String getEntityId();

    String getMetadata() throws IOException;

    XMLObject getEntityDescriptorElement();
}
