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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SamlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for loading a private key from a JKS keystore and returning the corresponding {@link Credential}
 * opensaml object.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class CredentialProvider {

    private final Logger logger = LoggerFactory.getLogger(CredentialProvider.class);

    private final CredentialResolver credentialResolver;

    private final String privateKey;

    public CredentialProvider(final String name, final String storePasswd, final String privateKeyPasswd) {
        URL url = CommonHelper.getURLFromName(name);
        KeyStore keyStore = loadKeyStore(url, storePasswd);
        this.privateKey = getPrivateKeyAlias(keyStore);
        Map<String, String> passwords = new HashMap<String, String>();
        passwords.put(this.privateKey, privateKeyPasswd);
        this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
    }

    public Credential getCredential() {
        try {
            CriteriaSet cs = new CriteriaSet();
            EntityIDCriteria criteria = new EntityIDCriteria(this.privateKey);
            cs.add(criteria);
            return this.credentialResolver.resolveSingle(cs);
        } catch (org.opensaml.xml.security.SecurityException e) {
            throw new SamlException("Can't obtain SP private key", e);
        }
    }

    private KeyStore loadKeyStore(final URL url, final String storePasswd) {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            return ks;
        } catch (Exception e) {
            this.logger.error("Error loading keystore", e);
            throw new SamlException("Error loading keystore", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    this.logger.debug("Error closing input stream of keystore", e);
                }
            }
        }
    }

    private String getPrivateKeyAlias(final KeyStore keyStore) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                return aliases.nextElement();
            } else {
                throw new SamlException("Keystore has no private keys");
            }
        } catch (KeyStoreException e) {
            throw new SamlException("Unable to get aliases from keyStore", e);
        }
    }
}
