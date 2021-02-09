package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

    private final String privateKeyAlias;

    public KeyStoreCredentialProvider(final SAML2Configuration configuration) {
        CommonHelper.assertNotBlank("keystorePassword", configuration.getPrivateKeyPassword());
        CommonHelper.assertNotBlank("privateKeyPassword", configuration.getPrivateKeyPassword());

        try (var inputStream = configuration.getKeystoreGenerator().retrieve()) {
            final var keyStoreType = configuration.getKeyStoreType() == null
                ? DEFAULT_KEYSTORE_TYPE
                : configuration.getKeyStoreType();
            final var keyStore = loadKeyStore(inputStream, configuration.getKeystorePassword(), keyStoreType);
            this.privateKeyAlias = getPrivateKeyAlias(keyStore, configuration.getKeyStoreAlias());
            final Map<String, String> passwords = new HashMap<>();
            passwords.put(this.privateKeyAlias, configuration.getPrivateKeyPassword());
            this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
        } catch (final Exception e) {
            throw new SAMLException("Error loading keystore", e);
        }
    }

    private static KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd, final String keyStoreType) {
        try {
            logger.debug("Loading keystore with type {}", keyStoreType);
            final var ks = KeyStore.getInstance(keyStoreType);
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            logger.debug("Loaded keystore with type {} with size {}", keyStoreType, ks.size());
            return ks;
        } catch (final Exception e) {
            throw new SAMLException("Error loading keystore", e);
        }
    }

    protected static String getPrivateKeyAlias(final KeyStore keyStore, final String keyStoreAlias) {
        try {
            final var aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                final var currentAlias = aliases.nextElement();
                if (keyStoreAlias != null) {
                    if (currentAlias.equalsIgnoreCase(keyStoreAlias)) {
                        return currentAlias;
                    }
                } else if (keyStore.entryInstanceOf(currentAlias, KeyStore.PrivateKeyEntry.class)) {
                    return currentAlias;
                }
            }
            throw new SAMLException("Keystore has no private keys to match the requested key alias " + keyStoreAlias);
        } catch (final KeyStoreException e) {
            throw new SAMLException("Unable to get aliases from keyStore", e);
        }
    }

    @Override
    public KeyInfo getKeyInfo() {
        final var serverCredential = getCredential();
        return generateKeyInfoForCredential(serverCredential);
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
        final var mgmr = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
        final var credential = getCredential();
        return mgmr.getDefaultManager().getFactory(credential).newInstance();
    }

    @Override
    public final Credential getCredential() {
        try {
            final var cs = new CriteriaSet();
            final var criteria = new EntityIdCriterion(this.privateKeyAlias);
            cs.add(criteria);
            return this.credentialResolver.resolveSingle(cs);
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
}
