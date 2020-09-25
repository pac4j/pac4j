package org.pac4j.config.ldaptive;

import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.Credential;
import org.ldaptive.FilterTemplate;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.ReturnAttributes;
import org.ldaptive.SearchConnectionValidator;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchScope;
import org.ldaptive.SimpleBindRequest;
import org.ldaptive.ad.extended.FastBindConnectionInitializer;
import org.ldaptive.auth.*;
import org.ldaptive.control.PasswordPolicyControl;
import org.ldaptive.pool.BindConnectionPassivator;
import org.ldaptive.pool.IdlePruneStrategy;
import org.ldaptive.sasl.Mechanism;
import org.ldaptive.sasl.SaslConfig;
import org.ldaptive.ssl.KeyStoreCredentialConfig;
import org.ldaptive.ssl.SslConfig;
import org.ldaptive.ssl.X509CredentialConfig;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;

import java.util.stream.Collectors;

/**
 * Copy/pasted from CAS server v5.0.4: Beans + LdapAuthenticationConfiguration classes, only the Ldaptive stuffs are kept.
 */
public class LdaptiveAuthenticatorBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdaptiveAuthenticatorBuilder.class);

    protected LdaptiveAuthenticatorBuilder() {
    }

    /*
     * ####################################################################################################################################
     * ####################################################################################################################################
     * From the LdapAuthenticationConfiguration class:
     * ####################################################################################################################################
     * ####################################################################################################################################
     */

    public static Authenticator getAuthenticator(final LdapAuthenticationProperties l) {
        if (l.getType() == LdapAuthenticationProperties.AuthenticationTypes.AD) {
            LOGGER.debug("Creating active directory authenticator for {}", l.getLdapUrl());
            return getActiveDirectoryAuthenticator(l);
        }
        if (l.getType() == LdapAuthenticationProperties.AuthenticationTypes.DIRECT) {
            LOGGER.debug("Creating direct-bind authenticator for {}", l.getLdapUrl());
            return getDirectBindAuthenticator(l);
        }
        if (l.getType() == LdapAuthenticationProperties.AuthenticationTypes.SASL) {
            LOGGER.debug("Creating SASL authenticator for {}", l.getLdapUrl());
            return getSaslAuthenticator(l);
        }
        if (l.getType() == LdapAuthenticationProperties.AuthenticationTypes.AUTHENTICATED) {
            LOGGER.debug("Creating authenticated authenticator for {}", l.getLdapUrl());
            return getAuthenticatedOrAnonSearchAuthenticator(l);
        }

        LOGGER.debug("Creating anonymous authenticator for {}", l.getLdapUrl());
        return getAuthenticatedOrAnonSearchAuthenticator(l);
    }

    private static Authenticator getSaslAuthenticator(final LdapAuthenticationProperties l) {
        final SearchDnResolver resolver = new SearchDnResolver();
        resolver.setBaseDn(l.getBaseDn());
        resolver.setSubtreeSearch(l.isSubtreeSearch());
        resolver.setAllowMultipleDns(l.isAllowMultipleDns());
        resolver.setConnectionFactory(newPooledConnectionFactory(l));
        resolver.setUserFilter(l.getUserFilter());
        return new Authenticator(resolver, getPooledBindAuthenticationHandler(l));
    }

    private static Authenticator getAuthenticatedOrAnonSearchAuthenticator(final LdapAuthenticationProperties l) {
        final SearchDnResolver resolver = new SearchDnResolver();
        resolver.setBaseDn(l.getBaseDn());
        resolver.setSubtreeSearch(l.isSubtreeSearch());
        resolver.setAllowMultipleDns(l.isAllowMultipleDns());
        resolver.setConnectionFactory(newPooledConnectionFactory(l));
        resolver.setUserFilter(l.getUserFilter());

        final Authenticator auth;
        if (CommonHelper.isBlank(l.getPrincipalAttributePassword())) {
            auth = new Authenticator(resolver, getPooledBindAuthenticationHandler(l));
        } else {
            auth = new Authenticator(resolver, getPooledCompareAuthenticationHandler(l));
        }

        if (l.isEnhanceWithEntryResolver()) {
            auth.setEntryResolver(newSearchEntryResolver(l));
        }
        return auth;
    }

    private static Authenticator getDirectBindAuthenticator(final LdapAuthenticationProperties l) {
        if (CommonHelper.isBlank(l.getDnFormat())) {
            throw new IllegalArgumentException("Dn format cannot be empty/blank for direct bind authentication");
        }
        final FormatDnResolver resolver = new FormatDnResolver(l.getDnFormat());
        final Authenticator authenticator = new Authenticator(resolver, getPooledBindAuthenticationHandler(l));

        if (l.isEnhanceWithEntryResolver()) {
            authenticator.setEntryResolver(newSearchEntryResolver(l));
        }
        return authenticator;
    }

    private static Authenticator getActiveDirectoryAuthenticator(final LdapAuthenticationProperties l) {
        if (CommonHelper.isBlank(l.getDnFormat())) {
            throw new IllegalArgumentException("Dn format cannot be empty/blank for active directory authentication");
        }
        final FormatDnResolver resolver = new FormatDnResolver(l.getDnFormat());
        final Authenticator authn = new Authenticator(resolver, getPooledBindAuthenticationHandler(l));

        if (l.isEnhanceWithEntryResolver()) {
            authn.setEntryResolver(newSearchEntryResolver(l));
        }
        return authn;
    }

    private static SimpleBindAuthenticationHandler getPooledBindAuthenticationHandler(final LdapAuthenticationProperties l) {
        final SimpleBindAuthenticationHandler handler = new SimpleBindAuthenticationHandler(newPooledConnectionFactory(l));
        handler.setAuthenticationControls(new PasswordPolicyControl());
        return handler;
    }

    private static CompareAuthenticationHandler getPooledCompareAuthenticationHandler(final LdapAuthenticationProperties l) {
        final CompareAuthenticationHandler handler = new CompareAuthenticationHandler(newPooledConnectionFactory(l));
        handler.setPasswordAttribute(l.getPrincipalAttributePassword());
        return handler;
    }

    /*
     * ####################################################################################################################################
     * ####################################################################################################################################
     * From the Beans class:
     * ####################################################################################################################################
     * ####################################################################################################################################
     */

    /**
     * New dn resolver entry resolver.
     *
     * @param l the ldap settings
     * @return the entry resolver
     */
    public static EntryResolver newSearchEntryResolver(final LdapAuthenticationProperties l) {
        final SearchEntryResolver entryResolver = new SearchEntryResolver();
        entryResolver.setBaseDn(l.getBaseDn());
        entryResolver.setUserFilter(l.getUserFilter());
        entryResolver.setSubtreeSearch(l.isSubtreeSearch());
        entryResolver.setConnectionFactory(LdaptiveAuthenticatorBuilder.newPooledConnectionFactory(l));
        return entryResolver;
    }


    /**
     * New connection config connection config.
     *
     * @param l the ldap properties
     * @return the connection config
     */
    public static ConnectionConfig newConnectionConfig(final AbstractLdapProperties l) {
        final ConnectionConfig cc = new ConnectionConfig();
        final String urls = Arrays.stream(l.getLdapUrl().split(",")).collect(Collectors.joining(" "));
        LOGGER.debug("Transformed LDAP urls from [{}] to [{}]", l.getLdapUrl(), urls);
        cc.setLdapUrl(urls);
        cc.setUseStartTLS(l.isUseStartTls());
        cc.setConnectTimeout(newDuration(l.getConnectTimeout()));

        if (l.getTrustCertificates() != null) {
            final X509CredentialConfig cfg = new X509CredentialConfig();
            cfg.setTrustCertificates(l.getTrustCertificates());
            cc.setSslConfig(new SslConfig(cfg));
        } else if (l.getKeystore() != null) {
            final KeyStoreCredentialConfig cfg = new KeyStoreCredentialConfig();
            cfg.setKeyStore(l.getKeystore());
            cfg.setKeyStorePassword(l.getKeystorePassword());
            cfg.setKeyStoreType(l.getKeystoreType());
            cc.setSslConfig(new SslConfig(cfg));
        } else {
            cc.setSslConfig(new SslConfig());
        }
        if (l.getSaslMechanism() != null) {
            final BindConnectionInitializer bc = new BindConnectionInitializer();
            final SaslConfig sc;
            switch (l.getSaslMechanism()) {
                case DIGEST_MD5:
                    sc = SaslConfig.builder().mechanism(Mechanism.DIGEST_MD5).realm(l.getSaslRealm()).build();
                    break;
                case CRAM_MD5:
                    sc = SaslConfig.builder().mechanism(Mechanism.CRAM_MD5).build();
                    break;
                case EXTERNAL:
                    sc = SaslConfig.builder().mechanism(Mechanism.EXTERNAL).build();
                    break;
                case GSSAPI:
                    sc = SaslConfig.builder().mechanism(Mechanism.GSSAPI).realm(l.getSaslRealm()).build();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown SASL mechanism " + l.getSaslMechanism().name());
            }
            sc.setAuthorizationId(l.getSaslAuthorizationId());
            sc.setMutualAuthentication(l.getSaslMutualAuth());
            sc.setQualityOfProtection(l.getSaslQualityOfProtection());
            sc.setSecurityStrength(l.getSaslSecurityStrength());
            bc.setBindSaslConfig(sc);
            cc.setConnectionInitializers(bc);
        } else if (CommonHelper.areEquals(l.getBindCredential(), "*") && CommonHelper.areEquals(l.getBindDn(), "*")) {
            cc.setConnectionInitializers(new FastBindConnectionInitializer());
        } else if (CommonHelper.isNotBlank(l.getBindDn()) && CommonHelper.isNotBlank(l.getBindCredential())) {
            cc.setConnectionInitializers(new BindConnectionInitializer(l.getBindDn(), new Credential(l.getBindCredential())));
        }
        return cc;
    }

    /**
     * New pooled connection factory.
     *
     * @param l the ldap properties
     * @return the pooled connection factory
     */
    public static PooledConnectionFactory newPooledConnectionFactory(final AbstractLdapProperties l) {
        final ConnectionConfig cc = newConnectionConfig(l);
        final PooledConnectionFactory cf = new PooledConnectionFactory(cc);
        cf.setBlockWaitTime(newDuration(l.getBlockWaitTime()));
        cf.setMinPoolSize(l.getMinPoolSize());
        cf.setMaxPoolSize(l.getMaxPoolSize());
        cf.setValidateOnCheckOut(l.isValidateOnCheckout());
        cf.setValidatePeriodically(l.isValidatePeriodically());

        final IdlePruneStrategy strategy = new IdlePruneStrategy();
        strategy.setIdleTime(newDuration(l.getIdleTime()));
        strategy.setPrunePeriod(newDuration(l.getPrunePeriod()));
        cf.setPruneStrategy(strategy);

        cf.setFailFastInitialize(l.isFailFast());

        final SearchConnectionValidator validator = new SearchConnectionValidator();
        validator.setValidatePeriod(newDuration(l.getValidatePeriod()));
        cf.setValidator(validator);

        if (CommonHelper.isNotBlank(l.getPoolPassivator())) {
            final AbstractLdapProperties.LdapConnectionPoolPassivator pass =
                AbstractLdapProperties.LdapConnectionPoolPassivator.valueOf(l.getPoolPassivator().toUpperCase());
            switch (pass) {
                case CLOSE:
                    // TODO: provide a property to disable pooling which return a DefaultConnectionFactory
                    // TODO: this is preferable to a pool of closed connections
                    cf.setPassivator(conn -> {
                        conn.close();
                        return true;
                    });
                    break;
                case BIND:
                    LOGGER.debug("Creating a bind passivator instance for the connection pool");
                    final SimpleBindRequest bindRequest = new SimpleBindRequest(l.getBindDn(), new Credential(l.getBindCredential()));
                    cf.setPassivator(new BindConnectionPassivator(bindRequest));
                    break;
                default:
                    break;
            }
        }

        LOGGER.debug("Initializing ldap connection pool for {} and bindDn {}", l.getLdapUrl(), l.getBindDn());
        cf.initialize();
        return cf;
    }

    /**
     * New duration.
     *
     * @param length the length in seconds.
     * @return the duration
     */
    public static Duration newDuration(final long length) {
        return Duration.ofSeconds(length);
    }

    /**
     * Builds a new request.
     *
     * @param baseDn the base dn
     * @param filter the filter
     * @return the search request
     */
    public static SearchRequest newSearchRequest(final String baseDn, final FilterTemplate filter) {
        final SearchRequest sr = new SearchRequest(baseDn, filter);
        // TODO: this argument should be a list of individual attribute names
        //sr.setBinaryAttributes(ReturnAttributes.ALL_USER.value());
        sr.setReturnAttributes(ReturnAttributes.ALL_USER.value());
        sr.setSearchScope(SearchScope.SUBTREE);
        return sr;
    }

    /**
     * Constructs a new search filter using filterQuery as a template and the username as a parameter.
     *
     * @param filterQuery the query filter
     * @param params      the username
     * @return Search filter with parameters applied.
     */
    public static FilterTemplate newSearchFilter(final String filterQuery, final String... params) {
        final FilterTemplate filter = new FilterTemplate();
        filter.setFilter(filterQuery);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                if (filter.getFilter().contains("{" + i + "}")) {
                    filter.setParameter(i, params[i]);
                } else {
                    filter.setParameter("user", params[i]);
                }
            }
        }
        LOGGER.debug("Constructed LDAP search filter [{}]", filter.format());
        return filter;
    }

    /**
     * New search operation .
     *
     * @param baseDn      the base dn
     * @param filterQuery the filter query
     * @param params      the params
     * @return the search executor
     */
    public static SearchOperation newSearchOperation(final String baseDn, final String filterQuery, final String... params) {
        final SearchOperation operation = new SearchOperation();
        operation.setRequest(SearchRequest.builder()
            .dn(baseDn)
            .filter(newSearchFilter(filterQuery, params))
            .returnAttributes(ReturnAttributes.ALL.value())
            .scope(SearchScope.SUBTREE)
            .build());
        return operation;
    }
}

