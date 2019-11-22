package org.pac4j.saml.metadata;

public interface Pac4JMetadataResolver extends org.opensaml.saml.metadata.resolver.MetadataResolver {

    void createParentDirectories() throws Exception;

}
