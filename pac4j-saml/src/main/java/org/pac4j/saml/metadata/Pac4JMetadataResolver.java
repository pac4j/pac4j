package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;

public interface Pac4JMetadataResolver extends MetadataResolver {

    void createParentDirectories() throws Exception;

}
