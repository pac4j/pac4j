package org.pac4j.saml.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;


/**
 * Base for all key store credential providers.
 * 
 * Responsible for loading a private key from a JKS keystore and returning the corresponding {@link Credential}
 * opensaml object.
 *
 * It can be used as-is; or a subclass can be used instead, providing different constructor arguments for convenience.
 * It is up to subclasses to provide an adequate input stream to the constructor, which should contain data of the JKS.
 * 
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public abstract class KeyStoreCredentialProvider implements CredentialProvider {

	private final Logger logger = LoggerFactory.getLogger(KeyStoreCredentialProvider.class);
	
    private final CredentialResolver credentialResolver;

    private final String privateKey;

    // ------------------------------------------------------------------------------------------------------------------------------------

    public KeyStoreCredentialProvider(final InputStream inputStream, final String storePasswd, final String privateKeyPasswd) {
        final KeyStore keyStore = loadKeyStore(inputStream, storePasswd);
        this.privateKey = getPrivateKeyAlias(keyStore);
        final Map<String, String> passwords = new HashMap<String, String>();
        passwords.put(this.privateKey, privateKeyPasswd);
        this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
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

    private KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd) {
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            return ks;
        } catch (final Exception e) {
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

    private String getPrivateKeyAlias(final KeyStore keyStore) {
        try {
            final Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                return aliases.nextElement();
            }

            throw new SAMLException("Keystore has no private keys");

        } catch (final KeyStoreException e) {
            throw new SAMLException("Unable to get aliases from keyStore", e);
        }
    }

}
