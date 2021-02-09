package org.pac4j.saml.metadata;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import java.io.File;

/**
 * This is {@link SAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
public interface SAML2MetadataSigner {
    default void sign(final EntityDescriptor descriptor) {}

    default void sign(final File metadataFile) {}
}
