package org.pac4j.saml.metadata;

public interface MetadataResolver extends org.opensaml.saml.metadata.resolver.MetadataResolver {

    void createParentDirectories() throws Exception;

}
