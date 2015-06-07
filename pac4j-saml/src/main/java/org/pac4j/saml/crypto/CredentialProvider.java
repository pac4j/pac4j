package org.pac4j.saml.crypto;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Defines operations required to provide and resolve credentials.
 * @author Misagh Moayyed
 * @since 1.7
 */
public interface CredentialProvider {

    KeyInfo getKeyInfo();

    CredentialResolver getCredentialResolver();

    KeyInfoCredentialResolver getKeyInfoCredentialResolver();

    KeyInfoGenerator getKeyInfoGenerator();

    Credential getCredential();
}
