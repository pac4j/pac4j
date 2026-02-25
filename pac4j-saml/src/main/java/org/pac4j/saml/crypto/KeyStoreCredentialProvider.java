package org.pac4j.saml.crypto;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Criterion;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.KeyStoreCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.KeyStoreUtils;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;

import java.util.HashMap;
import java.util.Objects;

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

    public KeyStoreCredentialProvider(final SAML2Configuration configuration) {
        CommonHelper.assertNotBlank("keystorePassword", configuration.getKeystorePassword());
        CommonHelper.assertNotBlank("privateKeyPassword", configuration.getPrivateKeyPassword());

        try (var inputStream = configuration.getKeystoreGenerator().retrieve()) {
            val keyStoreType = configuration.getKeyStoreType() == null
                ? DEFAULT_KEYSTORE_TYPE
                : configuration.getKeyStoreType();

            LOGGER.debug("Loading keystore with type {}", keyStoreType);
            val keyStore = KeyStoreUtils.loadKeyStore(inputStream, configuration.getKeystorePassword(), keyStoreType);
            LOGGER.debug("Loaded keystore with type {} with size {}", keyStoreType, keyStore.size());

            this.privateKeyAlias = KeyStoreUtils.findPrivateKeyAlias(keyStore, configuration.getKeyStoreAlias());

            val passwords = new HashMap<String, String>();
            passwords.put(this.privateKeyAlias, configuration.getPrivateKeyPassword());
            this.credentialResolver = new KeyStoreCredentialResolver(keyStore, passwords);
        } catch (final Exception e) {
            throw new TechnicalException("Error loading keystore", e);
        }
    }

    @Override
    public KeyInfo getKeyInfo() {
        val serverCredential = getCredential();
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
        val mgmr = DefaultSecurityConfigurationBootstrap.buildBasicKeyInfoGeneratorManager();
        val credential = getCredential();
        return Objects.requireNonNull(mgmr.getDefaultManager().getFactory(credential)).newInstance();
    }

    @Override
    public final Credential getCredential() {
        try {
            val cs = new CriteriaSet();
            Criterion criteria = new EntityIdCriterion(this.privateKeyAlias);
            cs.add(criteria);
            return this.credentialResolver.resolveSingle(cs);
        } catch (final ResolverException e) {
            throw new TechnicalException("Can't obtain SP private key", e);
        }
    }

    protected final KeyInfo generateKeyInfoForCredential(final Credential credential) {
        try {
            return getKeyInfoGenerator().generate(credential);
        } catch (final SecurityException e) {
            throw new TechnicalException("Unable to generate keyInfo from given credential", e);
        }
    }
}
