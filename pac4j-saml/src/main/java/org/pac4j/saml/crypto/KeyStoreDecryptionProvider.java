package org.pac4j.saml.crypto;

import lombok.val;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Provider returning well configured decrypter instances.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class KeyStoreDecryptionProvider implements DecryptionProvider {

    private static final ChainingEncryptedKeyResolver encryptedKeyResolver;
    private final CredentialProvider credentialProvider;

    static {
        final List<EncryptedKeyResolver> list = new ArrayList<>();
        list.add(new InlineEncryptedKeyResolver());
        list.add(new EncryptedElementTypeEncryptedKeyResolver());
        list.add(new SimpleRetrievalMethodEncryptedKeyResolver());
        encryptedKeyResolver = new ChainingEncryptedKeyResolver(list);
    }

    public KeyStoreDecryptionProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    @Override
    public final Decrypter build() {
        val encryptionCredential = this.credentialProvider.getCredential();
        final KeyInfoCredentialResolver resolver = new StaticKeyInfoCredentialResolver(encryptionCredential);
        val decrypter = new Decrypter(null, resolver, encryptedKeyResolver);
        decrypter.setRootInNewDocument(true);

        return decrypter;
    }
}
