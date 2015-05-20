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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.KeyInfo;
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
        final InputStream inputStream = CommonHelper.getInputStreamFromName(name);
        final KeyStore keyStore = loadKeyStore(inputStream, storePasswd);
        this.privateKey = getPrivateKeyAlias(keyStore);
        final Map<String, String> passwords = new HashMap<String, String>();
        passwords.put(this.privateKey, privateKeyPasswd);
        this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
    }

    public KeyInfo getKeyInfo() {
        final Credential serverCredential = getCredential();
        final KeyInfo keyInfo = generateKeyInfoForCredential(serverCredential);
        return keyInfo;
    }

    public CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        final KeyInfoCredentialResolver resolver = new
                StaticKeyInfoCredentialResolver(this.getCredential());
        return resolver;
    }

    public KeyInfoGenerator getKeyInfoGenerator() {
        final BasicKeyInfoGeneratorFactory factory = new BasicKeyInfoGeneratorFactory();
        factory.setEmitKeyNames(true);
        factory.setEmitEntityIDAsKeyName(true);
        factory.setEmitPublicDEREncodedKeyValue(true);
        factory.setEmitPublicKeyValue(true);
        return factory.newInstance();
    }

    protected KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            return getKeyInfoGenerator().generate(credential);
        } catch (org.opensaml.security.SecurityException e) {
            throw new SamlException("Unable to generate keyInfo from given credential", e);
        }
    }

    public Credential getCredential() {
        try {
            final CriteriaSet cs = new CriteriaSet();
            final EntityIdCriterion criteria = new EntityIdCriterion(this.privateKey);
            cs.add(criteria);
            final X509Credential creds = (X509Credential) this.credentialResolver.resolveSingle(cs);
            return creds;
        } catch (ResolverException e) {
            throw new SamlException("Can't obtain SP private key", e);
        }
    }

    private KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd) {
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
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
            final Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                return aliases.nextElement();
            }

            throw new SamlException("Keystore has no private keys");

        } catch (KeyStoreException e) {
            throw new SamlException("Unable to get aliases from keyStore", e);
        }
    }
}
