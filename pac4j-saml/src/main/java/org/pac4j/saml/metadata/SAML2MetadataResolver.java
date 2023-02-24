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
     * @return a {@link org.opensaml.saml.metadata.resolver.MetadataResolver} object
     */
    MetadataResolver resolve();

    /**
     * <p>getEntityId.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getEntityId();

    /**
     * <p>getMetadata.</p>
     *
     * @return a {@link java.lang.String} object
     */
    String getMetadata();

    /**
     * <p>getEntityDescriptorElement.</p>
     *
     * @return a {@link org.opensaml.core.xml.XMLObject} object
     */
    XMLObject getEntityDescriptorElement();
}
