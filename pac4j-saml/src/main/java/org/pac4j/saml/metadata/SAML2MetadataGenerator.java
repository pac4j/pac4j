package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Builds metadata and the relevant resolvers.
 *
 * @author Misagh Moayyed
 */
public interface SAML2MetadataGenerator {
    /**
     * <p>buildMetadataResolver.</p>
     *
     * @return a {@link org.opensaml.saml.metadata.resolver.MetadataResolver} object
     * @throws java.lang.Exception if any.
     */
    MetadataResolver buildMetadataResolver() throws Exception;

    /**
     * <p>getMetadata.</p>
     *
     * @param entityDescriptor a {@link org.opensaml.saml.saml2.metadata.EntityDescriptor} object
     * @return a {@link java.lang.String} object
     * @throws java.lang.Exception if any.
     */
    String getMetadata(EntityDescriptor entityDescriptor) throws Exception;

    /**
     * <p>buildEntityDescriptor.</p>
     *
     * @return a {@link org.opensaml.saml.saml2.metadata.EntityDescriptor} object
     */
    EntityDescriptor buildEntityDescriptor();

    /**
     * <p>storeMetadata.</p>
     *
     * @param metadata a {@link java.lang.String} object
     * @param force a boolean
     * @return a boolean
     * @throws java.lang.Exception if any.
     */
    boolean storeMetadata(String metadata, boolean force) throws Exception;
}
