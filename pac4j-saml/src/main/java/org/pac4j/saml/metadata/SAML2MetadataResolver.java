package org.pac4j.saml.metadata;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;

/**
 * Defines operations required to resolve metadata for idp and sp.
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface SAML2MetadataResolver {
    /**
     * <p>resolve.</p>
     *
     * @param force the force
     * @return a {@link MetadataResolver} object
     */
    MetadataResolver resolve(boolean force);

    /**
     * Resolve metadata resolver without force.
     *
     * @return the metadata resolver
     */
    default MetadataResolver resolve() {
        return resolve(false);
    }

    /**
     * <p>getEntityId.</p>
     *
     * @return a {@link String} object
     */
    String getEntityId();

    /**
     * <p>getMetadata.</p>
     *
     * @return a {@link String} object
     */
    String getMetadata();

    /**
     * <p>getEntityDescriptorElement.</p>
     *
     * @return a {@link XMLObject} object
     */
    XMLObject getEntityDescriptorElement();
}
