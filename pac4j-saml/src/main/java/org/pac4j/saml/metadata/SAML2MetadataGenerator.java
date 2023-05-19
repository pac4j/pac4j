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
     * @return a {@link MetadataResolver} object
     * @throws Exception if any.
     */
    MetadataResolver buildMetadataResolver() throws Exception;

    /**
     * <p>getMetadata.</p>
     *
     * @param entityDescriptor a {@link EntityDescriptor} object
     * @return a {@link String} object
     * @throws Exception if any.
     */
    String getMetadata(EntityDescriptor entityDescriptor) throws Exception;

    /**
     * <p>buildEntityDescriptor.</p>
     *
     * @return a {@link EntityDescriptor} object
     */
    EntityDescriptor buildEntityDescriptor();

    /**
     * <p>storeMetadata.</p>
     *
     * @param metadata a {@link String} object
     * @param force a boolean
     * @return a boolean
     * @throws Exception if any.
     */
    boolean storeMetadata(String metadata, boolean force) throws Exception;
}
