/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package org.pac4j.saml.crypto;

import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.security.credential.Credential;
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
        final List<EncryptedKeyResolver> list = new ArrayList<EncryptedKeyResolver>();
        list.add(new InlineEncryptedKeyResolver());
        list.add(new EncryptedElementTypeEncryptedKeyResolver());
        list.add(new SimpleRetrievalMethodEncryptedKeyResolver());
        encryptedKeyResolver = new ChainingEncryptedKeyResolver(list);
    }

    public KeyStoreDecryptionProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    public final Decrypter build() {
        final Credential encryptionCredential = this.credentialProvider.getCredential();
        final KeyInfoCredentialResolver resolver = new StaticKeyInfoCredentialResolver(encryptionCredential);
        final Decrypter decrypter = new Decrypter(null, resolver, encryptedKeyResolver);
        decrypter.setRootInNewDocument(true);

        return decrypter;
    }
}
