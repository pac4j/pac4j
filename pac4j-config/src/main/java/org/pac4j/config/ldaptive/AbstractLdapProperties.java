package org.pac4j.config.ldaptive;

import lombok.Getter;
import lombok.Setter;
import org.ldaptive.sasl.Mechanism;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SecurityStrength;

/**
 * Copy/pasted from CAS server v5.0.4 as is.
 *
 * @author bidou
 */
@Getter
@Setter
public abstract class AbstractLdapProperties {
    /**
     * The ldap connection pool passivator.
     */
    public enum LdapConnectionPoolPassivator {
        /**
         * No passivator.
         */
        NONE,
        /**
         * Close passivator.
         */
        CLOSE,
        /**
         * Bind passivator.
         */
        BIND
    }

    private String trustCertificates;

    private String keystore;
    private String keystorePassword;
    private String keystoreType;

    private int minPoolSize = 3;
    private int maxPoolSize = 10;
    private String poolPassivator;

    private boolean validateOnCheckout = true;
    private boolean validatePeriodically = true;
    private long validatePeriod = 300;

    private boolean failFast = true;
    private long idleTime = 600;
    private long prunePeriod = 10000;
    private long blockWaitTime = 6000;

    private String ldapUrl = "ldap://localhost:389";
    private boolean useStartTls;
    private long connectTimeout = 5000;

    private String providerClass;
    private boolean allowMultipleDns;

    private String bindDn;
    private String bindCredential;

    private String saslRealm;
    private Mechanism saslMechanism;
    private String saslAuthorizationId;

    private SecurityStrength saslSecurityStrength;
    private Boolean saslMutualAuth;
    private QualityOfProtection saslQualityOfProtection;
}

