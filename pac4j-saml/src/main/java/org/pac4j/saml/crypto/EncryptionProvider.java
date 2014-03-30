/*
  Copyright 2012 -2014 Michael Remond

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

import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.saml2.encryption.EncryptedElementTypeEncryptedKeyResolver;
import org.opensaml.xml.encryption.ChainingEncryptedKeyResolver;
import org.opensaml.xml.encryption.InlineEncryptedKeyResolver;
import org.opensaml.xml.encryption.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.keyinfo.StaticKeyInfoCredentialResolver;

/**
 * Provider returning well configured decrypter instances.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class EncryptionProvider {

    private static ChainingEncryptedKeyResolver encryptedKeyResolver = new ChainingEncryptedKeyResolver();

    static {
        encryptedKeyResolver.getResolverChain().add(new InlineEncryptedKeyResolver());
        encryptedKeyResolver.getResolverChain().add(new EncryptedElementTypeEncryptedKeyResolver());
        encryptedKeyResolver.getResolverChain().add(new SimpleRetrievalMethodEncryptedKeyResolver());
    }

    private final CredentialProvider credentialProvider;

    public EncryptionProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    public Decrypter buildDecrypter() {
        Credential encryptionCredential = this.credentialProvider.getCredential();
        KeyInfoCredentialResolver resolver = new StaticKeyInfoCredentialResolver(encryptionCredential);
        Decrypter decrypter = new Decrypter(null, resolver, encryptedKeyResolver);
        decrypter.setRootInNewDocument(true);

        return decrypter;
    }
}
