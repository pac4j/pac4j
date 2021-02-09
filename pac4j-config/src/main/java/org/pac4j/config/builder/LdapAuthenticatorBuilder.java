package org.pac4j.config.builder;

import org.ldaptive.ConnectionFactoryManager;
import org.ldaptive.sasl.Mechanism;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SecurityStrength;
import org.pac4j.config.ldaptive.LdapAuthenticationProperties;
import org.pac4j.config.ldaptive.LdaptiveAuthenticatorBuilder;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.ldap.profile.service.LdapProfileService;

import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for the LDAP authenticator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class LdapAuthenticatorBuilder extends AbstractBuilder {

    public LdapAuthenticatorBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryBuildLdapAuthenticator(final Map<String, Authenticator> authenticators) {
        for (var i = 0; i <= MAX_NUM_AUTHENTICATORS; i++) {
            final var type = getProperty(LDAP_TYPE, i);
            if (isNotBlank(type)) {
                final var ldapProp = buildLdapProperties(i);
                final var ldaptiveAuthenticator = LdaptiveAuthenticatorBuilder.getAuthenticator(ldapProp);

                final var authenticator = new LdapProfileService(ldaptiveAuthenticator, getProperty(LDAP_ATTRIBUTES, i));
                final var connectionFactoryManager =
                    (ConnectionFactoryManager) ldaptiveAuthenticator.getAuthenticationHandler();
                authenticator.setConnectionFactory(connectionFactoryManager.getConnectionFactory());
                authenticator.setUsersDn(getProperty(LDAP_USERS_DN, i));
                if (containsProperty(LDAP_PRINCIPAL_ATTRIBUTE_ID, i)) {
                    authenticator.setUsernameAttribute(getProperty(LDAP_PRINCIPAL_ATTRIBUTE_ID, i));
                }
                if (containsProperty(LDAP_PRINCIPAL_ATTRIBUTE_PASSWORD, i)) {
                    authenticator.setPasswordAttribute(getProperty(LDAP_PRINCIPAL_ATTRIBUTE_PASSWORD, i));
                }

                authenticators.put(concat("ldap", i), authenticator);
            }
        }
    }

    private LdapAuthenticationProperties buildLdapProperties(final int i) {
        final var ldapProp = new LdapAuthenticationProperties();
        final var type = getProperty(LDAP_TYPE, i);
        ldapProp.setType(LdapAuthenticationProperties.AuthenticationTypes.valueOf(type.toUpperCase()));
        ldapProp.setDnFormat(getProperty(LDAP_DN_FORMAT, i));
        if (containsProperty(LDAP_PRINCIPAL_ATTRIBUTE_PASSWORD, i)) {
            ldapProp.setPrincipalAttributePassword(getProperty(LDAP_PRINCIPAL_ATTRIBUTE_PASSWORD, i));
        }
        if (containsProperty(LDAP_SUBTREE_SEARCH, i)) {
            ldapProp.setSubtreeSearch(getPropertyAsBoolean(LDAP_SUBTREE_SEARCH, i));
        }
        ldapProp.setBaseDn(getProperty(LDAP_USERS_DN, i));
        ldapProp.setUserFilter(getProperty(LDAP_USER_FILTER, i));
        if (containsProperty(LDAP_ENHANCE_WITH_ENTRY_RESOLVER, i)) {
            ldapProp.setEnhanceWithEntryResolver(getPropertyAsBoolean(LDAP_ENHANCE_WITH_ENTRY_RESOLVER, i));
        }
        ldapProp.setLdapUrl(getProperty(LDAP_URL, i));
        if (containsProperty(LDAP_TRUST_CERTIFICATES, i)) {
            ldapProp.setTrustCertificates(getProperty(LDAP_TRUST_CERTIFICATES, i));
        }
        if (containsProperty(LDAP_KEYSTORE, i)) {
            ldapProp.setKeystore(getProperty(LDAP_KEYSTORE, i));
        }
        if (containsProperty(LDAP_KEYSTORE_PASSWORD, i)) {
            ldapProp.setKeystorePassword(getProperty(LDAP_KEYSTORE_PASSWORD, i));
        }
        if (containsProperty(LDAP_KEYSTORE_TYPE, i)) {
            ldapProp.setKeystoreType(getProperty(LDAP_KEYSTORE_TYPE, i));
        }
        if (containsProperty(LDAP_MIN_POOL_SIZE, i)) {
            ldapProp.setMinPoolSize(getPropertyAsInteger(LDAP_MIN_POOL_SIZE, i));
        }
        if (containsProperty(LDAP_MAX_POOL_SIZE, i)) {
            ldapProp.setMaxPoolSize(getPropertyAsInteger(LDAP_MAX_POOL_SIZE, i));
        }
        if (containsProperty(LDAP_POOL_PASSIVATOR, i)) {
            ldapProp.setPoolPassivator(getProperty(LDAP_POOL_PASSIVATOR, i));
        }
        if (containsProperty(LDAP_VALIDATE_ON_CHECKOUT, i)) {
            ldapProp.setValidateOnCheckout(getPropertyAsBoolean(LDAP_VALIDATE_ON_CHECKOUT, i));
        }
        if (containsProperty(LDAP_VALIDATE_PERIODICALLY, i)) {
            ldapProp.setValidatePeriodically(getPropertyAsBoolean(LDAP_VALIDATE_PERIODICALLY, i));
        }
        if (containsProperty(LDAP_VALIDATE_PERIOD, i)) {
            ldapProp.setValidatePeriod(getPropertyAsLong(LDAP_VALIDATE_PERIOD, i));
        }
        if (containsProperty(LDAP_FAIL_FAST, i)) {
            ldapProp.setFailFast(getPropertyAsBoolean(LDAP_FAIL_FAST, i));
        }
        if (containsProperty(LDAP_IDLE_TIME, i)) {
            ldapProp.setIdleTime(getPropertyAsLong(LDAP_IDLE_TIME, i));
        }
        if (containsProperty(LDAP_PRUNE_PERIOD, i)) {
            ldapProp.setPrunePeriod(getPropertyAsLong(LDAP_PRUNE_PERIOD, i));
        }
        if (containsProperty(LDAP_BLOCK_WAIT_TIME, i)) {
            ldapProp.setBlockWaitTime(getPropertyAsLong(LDAP_BLOCK_WAIT_TIME, i));
        }
        if (containsProperty(LDAP_USE_START_TLS, i)) {
            ldapProp.setUseStartTls(getPropertyAsBoolean(LDAP_USE_START_TLS, i));
        }
        if (containsProperty(LDAP_CONNECT_TIMEOUT, i)) {
            ldapProp.setConnectTimeout(getPropertyAsLong(LDAP_CONNECT_TIMEOUT, i));
        }
        if (containsProperty(LDAP_ALLOW_MULTIPLE_DNS, i)) {
            ldapProp.setAllowMultipleDns(getPropertyAsBoolean(LDAP_ALLOW_MULTIPLE_DNS, i));
        }
        if (containsProperty(LDAP_BIND_DN, i)) {
            ldapProp.setBindDn(getProperty(LDAP_BIND_DN, i));
        }
        if (containsProperty(LDAP_BIND_CREDENTIAL, i)) {
            ldapProp.setBindCredential(getProperty(LDAP_BIND_CREDENTIAL, i));
        }
        if (containsProperty(LDAP_SASL_REALM, i)) {
            ldapProp.setSaslRealm(getProperty(LDAP_SASL_REALM, i));
        }
        if (containsProperty(LDAP_SASL_MECHANISM, i)) {
            ldapProp.setSaslMechanism(Mechanism.valueOf(getProperty(LDAP_SASL_MECHANISM, i).toUpperCase()));
        }
        if (containsProperty(LDAP_SASL_AUTHORIZATION_ID, i)) {
            ldapProp.setSaslAuthorizationId(getProperty(LDAP_SASL_AUTHORIZATION_ID, i));
        }
        if (containsProperty(LDAP_SASL_SECURITY_STRENGTH, i)) {
            ldapProp.setSaslSecurityStrength(SecurityStrength.valueOf(getProperty(LDAP_SASL_SECURITY_STRENGTH, i).toUpperCase()));
        }
        if (containsProperty(LDAP_SASL_QUALITY_OF_PROTECTION, i)) {
            ldapProp.setSaslQualityOfProtection(QualityOfProtection.valueOf(getProperty(LDAP_SASL_QUALITY_OF_PROTECTION, i).toUpperCase()));
        }
        return ldapProp;
    }
}
