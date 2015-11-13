package org.pac4j.saml.dbcrypto;

import java.io.ByteArrayInputStream;
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
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;


/**
 * Alternative to {@link KeyStoreCredentialProvider}. Unlike {@link KeyStoreCredentialProvider}, it does not take a path to the keystore but the keystore binary content.
 *
 * TODO: We cannot extend {@link KeyStoreCredentialProvider} because it needs a path in the constructor. It would be good to have
 * a common ancestor to reuse some code. Most methods are just copied - duplicate code.
 * 
 * @author jkacer
 */
public class KeyStoreCredentialProvider2 implements CredentialProvider { 

	private final Logger logger = LoggerFactory.getLogger(KeyStoreCredentialProvider2.class);
	
    private final CredentialResolver credentialResolver;

    private final String privateKey;

    // ------------------------------------------------------------------------------------------------------------------------------------
    
	/**
	 * Creates a new keystore credential provider.
	 * 
	 * @param keystoreBinData
	 *            Binary data of a JKS keystore.
	 * @param storePasswd
	 *            Password for the store.
	 * @param privateKeyPasswd
	 *            Password for the private key.
	 */
    public KeyStoreCredentialProvider2(final byte[] keystoreBinData, final String storePasswd, final String privateKeyPasswd) {
    	final InputStream inputStream = new ByteArrayInputStream(keystoreBinData);
        final KeyStore keyStore = loadKeyStore(inputStream, storePasswd);
        this.privateKey = getPrivateKeyAlias(keyStore);
        final Map<String, String> passwords = new HashMap<String, String>();
        passwords.put(this.privateKey, privateKeyPasswd);
        this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
    }

    
    // Just copied from KeyStoreCredentialProvider
    @Override
    public KeyInfo getKeyInfo() {
        final Credential serverCredential = getCredential();
        final KeyInfo keyInfo = generateKeyInfoForCredential(serverCredential);
        return keyInfo;
    }

    // Just copied from KeyStoreCredentialProvider
    @Override
    public final CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    // Just copied from KeyStoreCredentialProvider
    @Override
    public KeyInfoCredentialResolver getKeyInfoCredentialResolver() {
        return DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();
    }

    // Just copied from KeyStoreCredentialProvider
    @Override
    public final KeyInfoGenerator getKeyInfoGenerator() {
        final NamedKeyInfoGeneratorManager mgmr = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
        final Credential credential = getCredential();
        return mgmr.getDefaultManager().getFactory(credential).newInstance();
    }

    // Just copied from KeyStoreCredentialProvider
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

    // Just copied from KeyStoreCredentialProvider
    protected final KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            return getKeyInfoGenerator().generate(credential);
        } catch (final org.opensaml.security.SecurityException e) {
            throw new SAMLException("Unable to generate keyInfo from given credential", e);
        }
    }

    // Just copied from KeyStoreCredentialProvider
    private KeyStore loadKeyStore(final InputStream inputStream, final String storePasswd) {
        try {
            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(inputStream, storePasswd == null ? null : storePasswd.toCharArray());
            return ks;
        } catch (final Exception e) {
            this.logger.error("Error loading keystore", e);
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

    // Just copied from KeyStoreCredentialProvider
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
