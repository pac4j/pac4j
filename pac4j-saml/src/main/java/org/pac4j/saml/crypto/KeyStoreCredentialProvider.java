package org.pac4j.saml.crypto;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Criterion;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;

/**
 * Class responsible for loading a private key from a JKS keystore and returning
 * the corresponding {@link Credential} opensaml object.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@Slf4j
public class KeyStoreCredentialProvider implements CredentialProvider {

    private static final String DEFAULT_KEYSTORE_TYPE = "JKS";

    private final CredentialResolver credentialResolver;

    private final String privateKeyAlias;

    /**
     * <p>Constructor for KeyStoreCredentialProvider.</p>
     *
     * @param configuration a {@link SAML2Configuration} object
     */
    public KeyStoreCredentialProvider(final SAML2Configuration configuration) {
        CommonHelper.assertNotBlank("keystorePassword", configuration.getKeystorePassword());
        CommonHelper.assertNotBlank("privateKeyPassword", configuration.getPrivateKeyPassword());

        try (var inputStream = configuration.getKeystoreGenerator().retrieve()) {
            val keyStoreType = configuration.getKeyStoreType() == null
                ? DEFAULT_KEYSTORE_TYPE
                : configuration.getKeyStoreType();
            val keyStore = loadKeyStore(inputStream, configuration.getKeystorePassword(), keyStoreType);
            this.privateKeyAlias = getPrivateKeyAlias(keyStore, configuration.getKeyStoreAlias());
            val passwords = new HashMap<String, String>();
            passwords.put(this.privateKeyAlias, configuration.getPrivateKeyPassword());
            this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
        } catch (final Exception e) {
            throw new SAMLException("Error loading keystore", e);
        }
    }

    private static KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd, final String keyStoreType) {
        try {
            LOGGER.debug("Loading keystore with type {}", keyStoreType);
            val ks = KeyStore.getInstance(keyStoreType);
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            LOGGER.debug("Loaded keystore with type {} with size {}", keyStoreType, ks.size());
            return ks;
        } catch (final Exception e) {
            throw new SAMLException("Error loading keystore", e);
        }
    }

    /**
     * <p>Getter for the field <code>privateKeyAlias</code>.</p>
     *
     * @param keyStore a {@link KeyStore} object
     * @param keyStoreAlias a {@link String} object
     * @return a {@link String} object
     */
    protected static String getPrivateKeyAlias(final KeyStore keyStore, final String keyStoreAlias) {
        try {
            val aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                val currentAlias = aliases.nextElement();
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

    /** {@inheritDoc} */
    @Override
    public KeyInfo getKeyInfo() {
        val serverCredential = getCredential();
        return generateKeyInfoForCredential(serverCredential);
    }

    /** {@inheritDoc} */
    @Override
    public final CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    @Override
    public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        return DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
    }

    /** {@inheritDoc} */
    @Override
    public final KeyInfoGenerator getKeyInfoGenerator() {
        val mgmr = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
        val credential = getCredential();
        return mgmr.getDefaultManager().getFactory(credential).newInstance();
    }

    /** {@inheritDoc} */
    @Override
    public final Credential getCredential() {
        try {
            val cs = new CriteriaSet();
            Criterion criteria = new EntityIdCriterion(this.privateKeyAlias);
            cs.add(criteria);
            return this.credentialResolver.resolveSingle(cs);
        } catch (final ResolverException e) {
            throw new SAMLException("Can't obtain SP private key", e);
        }
    }

    /**
     * <p>generateKeyInfoForCredential.</p>
     *
     * @param credential a {@link Credential} object
     * @return a {@link KeyInfo} object
     */
    protected final KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            return getKeyInfoGenerator().generate(credential);
        } catch (final org.opensaml.security.SecurityException e) {
            throw new SAMLException("Unable to generate keyInfo from given credential", e);
        }
    }
}
