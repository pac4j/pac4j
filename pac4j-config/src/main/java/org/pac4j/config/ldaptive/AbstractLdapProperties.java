package org.pac4j.config.ldaptive;

import org.ldaptive.sasl.Mechanism;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SecurityStrength;

/**
 * Copy/pasted from CAS server v5.0.4 as is.
 *
 * @author bidou
 */
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

    /**
     * <p>Getter for the field <code>poolPassivator</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPoolPassivator() {
        return poolPassivator;
    }

    /**
     * <p>Setter for the field <code>poolPassivator</code>.</p>
     *
     * @param poolPassivator a {@link java.lang.String} object
     */
    public void setPoolPassivator(final String poolPassivator) {
        this.poolPassivator = poolPassivator;
    }

    /**
     * <p>Getter for the field <code>bindDn</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBindDn() {
        return bindDn;
    }

    /**
     * <p>Setter for the field <code>bindDn</code>.</p>
     *
     * @param bindDn a {@link java.lang.String} object
     */
    public void setBindDn(final String bindDn) {
        this.bindDn = bindDn;
    }

    /**
     * <p>Getter for the field <code>bindCredential</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getBindCredential() {
        return bindCredential;
    }

    /**
     * <p>Setter for the field <code>bindCredential</code>.</p>
     *
     * @param bindCredential a {@link java.lang.String} object
     */
    public void setBindCredential(final String bindCredential) {
        this.bindCredential = bindCredential;
    }

    /**
     * <p>Getter for the field <code>providerClass</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getProviderClass() {
        return providerClass;
    }

    /**
     * <p>Setter for the field <code>providerClass</code>.</p>
     *
     * @param providerClass a {@link java.lang.String} object
     */
    public void setProviderClass(final String providerClass) {
        this.providerClass = providerClass;
    }

    /**
     * <p>isAllowMultipleDns.</p>
     *
     * @return a boolean
     */
    public boolean isAllowMultipleDns() {
        return allowMultipleDns;
    }

    /**
     * <p>Setter for the field <code>allowMultipleDns</code>.</p>
     *
     * @param allowMultipleDns a boolean
     */
    public void setAllowMultipleDns(final boolean allowMultipleDns) {
        this.allowMultipleDns = allowMultipleDns;
    }

    /**
     * <p>Getter for the field <code>prunePeriod</code>.</p>
     *
     * @return a long
     */
    public long getPrunePeriod() {
        return prunePeriod;
    }

    /**
     * <p>Setter for the field <code>prunePeriod</code>.</p>
     *
     * @param prunePeriod a long
     */
    public void setPrunePeriod(final long prunePeriod) {
        this.prunePeriod = prunePeriod;
    }

    /**
     * <p>Getter for the field <code>trustCertificates</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getTrustCertificates() {
        return trustCertificates;
    }

    /**
     * <p>Setter for the field <code>trustCertificates</code>.</p>
     *
     * @param trustCertificates a {@link java.lang.String} object
     */
    public void setTrustCertificates(final String trustCertificates) {
        this.trustCertificates = trustCertificates;
    }

    /**
     * <p>Getter for the field <code>keystore</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getKeystore() {
        return keystore;
    }

    /**
     * <p>Setter for the field <code>keystore</code>.</p>
     *
     * @param keystore a {@link java.lang.String} object
     */
    public void setKeystore(final String keystore) {
        this.keystore = keystore;
    }

    /**
     * <p>Getter for the field <code>keystorePassword</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getKeystorePassword() {
        return keystorePassword;
    }

    /**
     * <p>Setter for the field <code>keystorePassword</code>.</p>
     *
     * @param keystorePassword a {@link java.lang.String} object
     */
    public void setKeystorePassword(final String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    /**
     * <p>Getter for the field <code>keystoreType</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getKeystoreType() {
        return keystoreType;
    }

    /**
     * <p>Setter for the field <code>keystoreType</code>.</p>
     *
     * @param keystoreType a {@link java.lang.String} object
     */
    public void setKeystoreType(final String keystoreType) {
        this.keystoreType = keystoreType;
    }

    /**
     * <p>Getter for the field <code>minPoolSize</code>.</p>
     *
     * @return a int
     */
    public int getMinPoolSize() {
        return minPoolSize;
    }

    /**
     * <p>Setter for the field <code>minPoolSize</code>.</p>
     *
     * @param minPoolSize a int
     */
    public void setMinPoolSize(final int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    /**
     * <p>Getter for the field <code>maxPoolSize</code>.</p>
     *
     * @return a int
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * <p>Setter for the field <code>maxPoolSize</code>.</p>
     *
     * @param maxPoolSize a int
     */
    public void setMaxPoolSize(final int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * <p>isValidateOnCheckout.</p>
     *
     * @return a boolean
     */
    public boolean isValidateOnCheckout() {
        return validateOnCheckout;
    }

    /**
     * <p>Setter for the field <code>validateOnCheckout</code>.</p>
     *
     * @param validateOnCheckout a boolean
     */
    public void setValidateOnCheckout(final boolean validateOnCheckout) {
        this.validateOnCheckout = validateOnCheckout;
    }

    /**
     * <p>isValidatePeriodically.</p>
     *
     * @return a boolean
     */
    public boolean isValidatePeriodically() {
        return validatePeriodically;
    }

    /**
     * <p>Setter for the field <code>validatePeriodically</code>.</p>
     *
     * @param validatePeriodically a boolean
     */
    public void setValidatePeriodically(final boolean validatePeriodically) {
        this.validatePeriodically = validatePeriodically;
    }

    /**
     * <p>Getter for the field <code>validatePeriod</code>.</p>
     *
     * @return a long
     */
    public long getValidatePeriod() {
        return validatePeriod;
    }

    /**
     * <p>Setter for the field <code>validatePeriod</code>.</p>
     *
     * @param validatePeriod a long
     */
    public void setValidatePeriod(final long validatePeriod) {
        this.validatePeriod = validatePeriod;
    }

    /**
     * <p>isFailFast.</p>
     *
     * @return a boolean
     */
    public boolean isFailFast() {
        return failFast;
    }

    /**
     * <p>Setter for the field <code>failFast</code>.</p>
     *
     * @param failFast a boolean
     */
    public void setFailFast(final boolean failFast) {
        this.failFast = failFast;
    }

    /**
     * <p>Getter for the field <code>idleTime</code>.</p>
     *
     * @return a long
     */
    public long getIdleTime() {
        return idleTime;
    }

    /**
     * <p>Setter for the field <code>idleTime</code>.</p>
     *
     * @param idleTime a long
     */
    public void setIdleTime(final long idleTime) {
        this.idleTime = idleTime;
    }

    /**
     * <p>Getter for the field <code>blockWaitTime</code>.</p>
     *
     * @return a long
     */
    public long getBlockWaitTime() {
        return blockWaitTime;
    }

    /**
     * <p>Setter for the field <code>blockWaitTime</code>.</p>
     *
     * @param blockWaitTime a long
     */
    public void setBlockWaitTime(final long blockWaitTime) {
        this.blockWaitTime = blockWaitTime;
    }

    /**
     * <p>Getter for the field <code>ldapUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLdapUrl() {
        return ldapUrl;
    }

    /**
     * <p>Setter for the field <code>ldapUrl</code>.</p>
     *
     * @param ldapUrl a {@link java.lang.String} object
     */
    public void setLdapUrl(final String ldapUrl) {
        this.ldapUrl = ldapUrl;
    }

    /**
     * <p>isUseStartTls.</p>
     *
     * @return a boolean
     */
    public boolean isUseStartTls() {
        return useStartTls;
    }

    /**
     * <p>Setter for the field <code>useStartTls</code>.</p>
     *
     * @param useStartTls a boolean
     */
    public void setUseStartTls(final boolean useStartTls) {
        this.useStartTls = useStartTls;
    }

    /**
     * <p>Getter for the field <code>connectTimeout</code>.</p>
     *
     * @return a long
     */
    public long getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * <p>Setter for the field <code>connectTimeout</code>.</p>
     *
     * @param connectTimeout a long
     */
    public void setConnectTimeout(final long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * <p>Getter for the field <code>saslRealm</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSaslRealm() {
        return saslRealm;
    }

    /**
     * <p>Setter for the field <code>saslRealm</code>.</p>
     *
     * @param saslRealm a {@link java.lang.String} object
     */
    public void setSaslRealm(final String saslRealm) {
        this.saslRealm = saslRealm;
    }

    /**
     * <p>Getter for the field <code>saslMechanism</code>.</p>
     *
     * @return a {@link org.ldaptive.sasl.Mechanism} object
     */
    public Mechanism getSaslMechanism() {
        return saslMechanism;
    }

    /**
     * <p>Setter for the field <code>saslMechanism</code>.</p>
     *
     * @param saslMechanism a {@link org.ldaptive.sasl.Mechanism} object
     */
    public void setSaslMechanism(final Mechanism saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    /**
     * <p>Getter for the field <code>saslAuthorizationId</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSaslAuthorizationId() {
        return saslAuthorizationId;
    }

    /**
     * <p>Setter for the field <code>saslAuthorizationId</code>.</p>
     *
     * @param saslAuthorizationId a {@link java.lang.String} object
     */
    public void setSaslAuthorizationId(final String saslAuthorizationId) {
        this.saslAuthorizationId = saslAuthorizationId;
    }

    /**
     * <p>Getter for the field <code>saslSecurityStrength</code>.</p>
     *
     * @return a {@link org.ldaptive.sasl.SecurityStrength} object
     */
    public SecurityStrength getSaslSecurityStrength() {
        return saslSecurityStrength;
    }

    /**
     * <p>Setter for the field <code>saslSecurityStrength</code>.</p>
     *
     * @param saslSecurityStrength a {@link org.ldaptive.sasl.SecurityStrength} object
     */
    public void setSaslSecurityStrength(final SecurityStrength saslSecurityStrength) {
        this.saslSecurityStrength = saslSecurityStrength;
    }

    /**
     * <p>Getter for the field <code>saslQualityOfProtection</code>.</p>
     *
     * @return a {@link org.ldaptive.sasl.QualityOfProtection} object
     */
    public QualityOfProtection getSaslQualityOfProtection() {
        return saslQualityOfProtection;
    }

    /**
     * <p>Setter for the field <code>saslQualityOfProtection</code>.</p>
     *
     * @param saslQualityOfProtection a {@link org.ldaptive.sasl.QualityOfProtection} object
     */
    public void setSaslQualityOfProtection(final QualityOfProtection saslQualityOfProtection) {
        this.saslQualityOfProtection = saslQualityOfProtection;
    }

    /**
     * <p>Setter for the field <code>saslMutualAuth</code>.</p>
     *
     * @param saslMutualAuth a {@link java.lang.Boolean} object
     */
    public void setSaslMutualAuth(final Boolean saslMutualAuth) {
        this.saslMutualAuth = saslMutualAuth;
    }

    /**
     * <p>Getter for the field <code>saslMutualAuth</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getSaslMutualAuth() {
        return saslMutualAuth;
    }
}

