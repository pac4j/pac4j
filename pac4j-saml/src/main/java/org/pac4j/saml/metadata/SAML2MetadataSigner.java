package org.pac4j.saml.metadata;

import org.opensaml.xmlsec.signature.SignableXMLObject;

import java.io.File;

/**
 * This is {@link org.pac4j.saml.metadata.SAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public interface SAML2MetadataSigner {
    /**
     * <p>sign.</p>
     *
     * @param descriptor a {@link org.opensaml.xmlsec.signature.SignableXMLObject} object
     */
    void sign(SignableXMLObject descriptor);

    /**
     * <p>sign.</p>
     *
     * @param metadataFile a {@link java.io.File} object
     */
    void sign(File metadataFile);

    /**
     * <p>sign.</p>
     *
     * @param metadata a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    String sign(String metadata);
}
