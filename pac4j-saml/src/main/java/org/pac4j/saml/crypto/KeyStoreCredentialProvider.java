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
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Class responsible for loading a private key from a JKS keystore and returning the corresponding {@link Credential}
 * opensaml object.
 * 
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class KeyStoreCredentialProvider implements CredentialProvider {

    private static final Logger logger = LoggerFactory.getLogger(KeyStoreCredentialProvider.class);

    private static final String DEFAULT_KEYSTORE_TYPE = "JKS";

    private final CredentialResolver credentialResolver;

    private final String privateKey;

    public KeyStoreCredentialProvider(final String keyStoreAlias, final String keyStoreType,
                                      final Resource keyStoreResource, final String storePasswd, final String privateKeyPasswd) {
        CommonHelper.assertNotNull("keyStoreResource", keyStoreResource);
        CommonHelper.assertNotBlank("storePasswd", storePasswd);
        CommonHelper.assertNotBlank("privateKeyPasswd", privateKeyPasswd);

        InputStream inputStream = null;
        try {
            inputStream = keyStoreResource.getInputStream();
            final KeyStore keyStore = loadKeyStore(inputStream, storePasswd, keyStoreType);
            this.privateKey = getPrivateKeyAlias(keyStore, keyStoreAlias);
            final Map<String, String> passwords = new HashMap<>();
            passwords.put(this.privateKey, privateKeyPasswd);
            this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
        } catch (final IOException e) {
            throw new SAMLException("Error loading keystore", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    this.logger.debug("Error closing input stream of keystore", e);
                }
            }
        }
    }

    public KeyStoreCredentialProvider(final SAML2ClientConfiguration configuration) {
        this(configuration.getKeyStoreAlias(), 
            configuration.getKeyStoreType() == null ? DEFAULT_KEYSTORE_TYPE : configuration.getKeyStoreType(),
            configuration.getKeystoreResource(), configuration.getKeystorePassword(), configuration.getPrivateKeyPassword());
    }

    @Override
    public KeyInfo getKeyInfo() {
        final Credential serverCredential = getCredential();
        final KeyInfo keyInfo = generateKeyInfoForCredential(serverCredential);
        return keyInfo;
    }

    @Override
    public final CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    @Override
    public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        return DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
    }

    @Override
    public final KeyInfoGenerator getKeyInfoGenerator() {
        final NamedKeyInfoGeneratorManager mgmr = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
        final Credential credential = getCredential();
        return mgmr.getDefaultManager().getFactory(credential).newInstance();
    }

    @Override
    public final Credential getCredential() {
        try {
            final CriteriaSet cs = new CriteriaSet();
            final EntityIdCriterion criteria = new EntityIdCriterion(this.privateKey);
            cs.add(criteria);
            final X509Credential creds = (X509Credential) this.credentialResolver.resolveSingle(cs);
            return creds;
        } catch (final ResolverException e) {
            throw new SAMLException("Can't obtain SP private key", e);
        }
    }

    protected final KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            return getKeyInfoGenerator().generate(credential);
        } catch (final org.opensaml.security.SecurityException e) {
            throw new SAMLException("Unable to generate keyInfo from given credential", e);
        }
    }

    private KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd, final String keyStoreType) {
        try {
            final KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            return ks;
        } catch (final Exception e) {
            throw new SAMLException("Error loading keystore", e);
        }
    }

    protected static String getPrivateKeyAlias(final KeyStore keyStore, final String keyStoreAlias) {
        try {
            final Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                final String currentAlias = aliases.nextElement();
                if (keyStoreAlias != null) {
                    if (currentAlias.equalsIgnoreCase(keyStoreAlias)) {
                        return currentAlias;
                    }
                } else if (keyStore.entryInstanceOf(currentAlias, KeyStore.PrivateKeyEntry.class)) {
                    return currentAlias;
                }
            }

            throw new SAMLException("Keystore has no private keys");

        } catch (final KeyStoreException e) {
            throw new SAMLException("Unable to get aliases from keyStore", e);
        }
    }
}
