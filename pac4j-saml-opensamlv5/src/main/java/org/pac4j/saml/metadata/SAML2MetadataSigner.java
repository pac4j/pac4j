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
    void sign(SignableXMLObject descriptor);

    void sign(File metadataFile);

    String sign(String metadata);
}
