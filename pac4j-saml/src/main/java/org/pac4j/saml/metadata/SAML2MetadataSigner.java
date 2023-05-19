package org.pac4j.saml.metadata;

import org.opensaml.xmlsec.signature.SignableXMLObject;

import java.io.File;

/**
 * This is {@link SAML2MetadataSigner}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public interface SAML2MetadataSigner {
    /**
     * <p>sign.</p>
     *
     * @param descriptor a {@link SignableXMLObject} object
     */
    void sign(SignableXMLObject descriptor);

    /**
     * <p>sign.</p>
     *
     * @param metadataFile a {@link File} object
     */
    void sign(File metadataFile);

    /**
     * <p>sign.</p>
     *
     * @param metadata a {@link String} object
     * @return a {@link String} object
     */
    String sign(String metadata);
}
