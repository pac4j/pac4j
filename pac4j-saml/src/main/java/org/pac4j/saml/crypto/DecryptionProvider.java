package org.pac4j.saml.crypto;

import org.opensaml.saml.saml2.encryption.Decrypter;

/**
 * Builds the decryption context.
 * @author Misagh Moayyed
 * @since 1.7
 */
@FunctionalInterface
public interface DecryptionProvider {
    Decrypter build();
}
