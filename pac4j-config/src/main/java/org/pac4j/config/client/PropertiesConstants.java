package org.pac4j.config.client;

/**
 * Properties constants for the {@link PropertiesConfigFactory}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public interface PropertiesConstants {

    String FACEBOOK_ID = "facebook.id";
    String FACEBOOK_SECRET = "facebook.secret";
    String FACEBOOK_SCOPE = "facebook.scope";
    String FACEBOOK_FIELDS = "facebook.fields";

    String TWITTER_ID = "twitter.id";
    String TWITTER_SECRET = "twitter.secret";

    String GITHUB_ID = "github.id";
    String GITHUB_SECRET = "github.secret";

    String DROPBOX_ID = "dropbox.id";
    String DROPBOX_SECRET = "dropbox.secret";

    String WINDOWSLIVE_ID = "windowslive.id";
    String WINDOWSLIVE_SECRET = "windowslive.secret";

    String YAHOO_ID = "yahoo.id";
    String YAHOO_SECRET = "yahoo.secret";

    String LINKEDIN_ID = "linkedin.id";
    String LINKEDIN_SECRET = "linkedin.secret";
    String LINKEDIN_FIELDS = "linkedin.fields";
    String LINKEDIN_SCOPE = "linkedin.scope";

    String FOURSQUARE_ID = "foursquare.id";
    String FOURSQUARE_SECRET = "foursquare.secret";

    String GOOGLE_ID = "google.id";
    String GOOGLE_SECRET = "google.secret";
    String GOOGLE_SCOPE = "google.scope";

    String AUTHENTICATOR_TEST_TOKEN = "testToken";
    String AUTHENTICATOR_TEST_USERNAME_PASSWORD = "testUsernamePassword";

    String SAML_KEYSTORE_PASSWORD = "saml.keystorePassword";
    String SAML_PRIVATE_KEY_PASSWORD = "saml.privateKeyPassword";
    String SAML_KEYSTORE_PATH = "saml.keystorePath";
    String SAML_IDENTITY_PROVIDER_METADATA_PATH = "saml.identityProviderMetadataPath";
    String SAML_MAXIMUM_AUTHENTICATION_LIFETIME = "saml.maximumAuthenticationLifetime";
    String SAML_SERVICE_PROVIDER_ENTITY_ID = "saml.serviceProviderEntityId";
    String SAML_SERVICE_PROVIDER_METADATA_PATH = "saml.serviceProviderMetadataPath";
    String SAML_AUTHN_REQUEST_BINDING_TYPE = "saml.authnRequestBindingType";
    String SAML_KEYSTORE_ALIAS = "saml.keystoreAlias";

    String CAS_LOGIN_URL = "cas.loginUrl";
    String CAS_PROTOCOL = "cas.protocol";

    String OIDC_TYPE = "oidc.type";
    String OIDC_GOOGLE_TYPE = "google";
    String OIDC_AZURE_TYPE = "azure";
    String OIDC_AZURE_TENANT = "oidc.azure.tenant";
    String OIDC_ID = "oidc.id";
    String OIDC_SECRET = "oidc.secret";
    String OIDC_SCOPE = "oidc.scope";
    String OIDC_DISCOVERY_URI = "oidc.discoveryUri";
    String OIDC_USE_NONCE = "oidc.useNonce";
    String OIDC_PREFERRED_JWS_ALGORITHM = "oidc.preferredJwsAlgorithm";
    String OIDC_MAX_CLOCK_SKEW = "oidc.maxClockSkew";
    String OIDC_CLIENT_AUTHENTICATION_METHOD = "oidc.clientAuthenticationMethod";
    String OIDC_CUSTOM_PARAM_KEY = "oidc.customParamKey";
    String OIDC_CUSTOM_PARAM_VALUE = "oidc.customParamValue";

    String FORMCLIENT_AUTHENTICATOR = "formClient.authenticator";
    String FORMCLIENT_LOGIN_URL = "formClient.loginUrl";
    String FORMCLIENT_USERNAME_PARAMETER = "formClient.usernameParameter";
    String FORMCLIENT_PASSWORD_PARAMETER = "formClient.passwordParameter";

    String INDIRECTBASICAUTH_AUTHENTICATOR = "indirectBasicAuth.authenticator";
    String INDIRECTBASICAUTH_REALM_NAME = "indirectBasicAuth.realName";

    String ANONYMOUS = "anonymous";

    String DIRECTBASICAUTH_AUTHENTICATOR = "directBasicAuth.authenticator";

    String LDAP_TYPE = "ldap.type";
    String LDAP_DN_FORMAT = "ldap.dnFormat";
    String LDAP_ATTRIBUTES = "ldap.principalAttributes";
    String LDAP_PRINCIPAL_ATTRIBUTE_ID = "ldap.principalAttributeId";
    String LDAP_PRINCIPAL_ATTRIBUTE_PASSWORD = "ldap.principalAttributePassword";
    String LDAP_SUBTREE_SEARCH = "ldap.subtreeSearch";
    String LDAP_USERS_DN = "ldap.usersDn";
    String LDAP_USER_FILTER = "ldap.userFilter";
    String LDAP_ENHANCE_WITH_ENTRY_RESOLVER = "ldap.enhanceWithEntryResolver";
    String LDAP_TRUST_CERTIFICATES = "ldap.trustCertificates";
    String LDAP_KEYSTORE = "ldap.keystore";
    String LDAP_KEYSTORE_PASSWORD = "ldap.keystorePassword";
    String LDAP_KEYSTORE_TYPE = "ldap.keystoreType";
    String LDAP_MIN_POOL_SIZE = "ldap.minPoolSize";
    String LDAP_MAX_POOL_SIZE = "ldap.maxPoolSize";
    String LDAP_POOL_PASSIVATOR = "ldap.poolPassivator";
    String LDAP_VALIDATE_ON_CHECKOUT = "ldap.validateOnCheckout";
    String LDAP_VALIDATE_PERIODICALLY = "ldap.validatePeriodically";
    String LDAP_VALIDATE_PERIOD = "ldap.validatePeriod";
    String LDAP_FAIL_FAST = "ldap.failFast";
    String LDAP_IDLE_TIME = "ldap.idleTime";
    String LDAP_PRUNE_PERIOD = "ldap.prunePeriod";
    String LDAP_BLOCK_WAIT_TIME = "ldap.blockWaitTime";
    String LDAP_URL = "ldap.url";
    String LDAP_USE_SSL = "ldap.useSsl";
    String LDAP_USE_START_TLS = "ldap.useStartTls";
    String LDAP_CONNECT_TIMEOUT = "ldap.connectTimeout";
    String LDAP_PROVIDER_CLASS = "ldap.providerClass";
    String LDAP_ALLOW_MULTIPLE_DNS = "ldap.allowMultipleDns";
    String LDAP_BIND_DN = "ldap.bindDn";
    String LDAP_BIND_CREDENTIAL = "ldap.bindCredential";
    String LDAP_SASL_REALM = "ldap.saslRealm";
    String LDAP_SASL_MECHANISM = "ldap.saslMechanism";
    String LDAP_SASL_AUTHORIZATION_ID = "ldap.saslAuthorizationId";
    String LDAP_SASL_SECURITY_STRENGTH = "ldap.saslSecurityStrength";
    String LDAP_SASL_QUALITY_OF_PROTECTION = "ldap.saslQualityOfProtection";

    String DB_DATASOURCE_CLASS_NAME = "db.dataSourceClassName";
    String DB_JDBC_URL = "db.jdbcUrl";
    String DB_ATTRIBUTES = "db.userAttributes";
    String DB_USER_ID_ATTRIBUTE = "db.userIdAttribute";
    String DB_USERNAME_ATTRIBUTE = "db.usernameAttribute";
    String DB_USER_PASSWORD_ATTRIBUTE = "db.userPasswordAttribute";
    String DB_USERS_TABLE = "db.usersTable";
    String DB_USERNAME = "db.username";
    String DB_PASSWORD = "db.password";
    String DB_AUTO_COMMIT = "db.autoCommit";
    String DB_CONNECTION_TIMEOUT = "db.connectionTimeout";
    String DB_IDLE_TIMEOUT = "db.idleTimeout";
    String DB_MAX_LIFETIME = "db.maxLifetime";
    String DB_CONNECTION_TEST_QUERY = "db.connectionTestQuery";
    String DB_MINIMUM_IDLE = "db.minimumIdle";
    String DB_MAXIMUM_POOL_SIZE = "db.maximumPoolSize";
    String DB_POOL_NAME = "db.poolName";
    String DB_INITIALIZATION_FAIL_TIMEOUT = "db.initializationFailTimeout";
    String DB_ISOLATE_INTERNAL_QUERIES = "db.isolateInternalQueries";
    String DB_ALLOW_POOL_SUSPENSION = "db.allowPoolSuspension";
    String DB_READ_ONLY = "db.readOnly";
    String DB_REGISTER_MBEANS = "db.registerMbeans";
    String DB_CATALOG = "db.catalog";
    String DB_CONNECTION_INIT_SQL = "db.connectionInitSql";
    String DB_DRIVER_CLASS_NAME = "db.driverClassName";
    String DB_TRANSACTION_ISOLATION = "db.transactionIsolation";
    String DB_VALIDATION_TIMEOUT = "db.validationTimeout";
    String DB_LEAK_DETECTION_THRESHOLD = "db.leakDetectionThreshold";
    String DB_CUSTOM_PARAM_KEY = "db.customParamKey";
    String DB_CUSTOM_PARAM_VALUE = "db.customParamValue";
    String DB_LOGIN_TIMEOUT = "db.loginTimeout";
    String DB_DATASOURCE_JNDI = "db.dataSourceJndi";
    String DB_PASSWORD_ENCODER = "db.passwordEncoder";

    String REST_URL = "rest.url";

    String SPRING_ENCODER = "encoder.spring";
    String SPRING_ENCODER_TYPE = "encoder.spring.type";
    enum SpringEncoderType {BCRYPT, NOOP, PBKDF2, SCRYPT, STANDARD}
    String SPRING_ENCODER_BCRYPT_LENGTH = "encoder.spring.bcrypt.length";
    String SPRING_ENCODER_PBKDF2_SECRET = "encoder.spring.pbkdf2.secret";
    String SPRING_ENCODER_PBKDF2_ITERATIONS = "encoder.spring.pbkdf2.iterations";
    String SPRING_ENCODER_PBKDF2_HASH_WIDTH = "encoder.spring.pbkdf2.hashWidth";
    String SPRING_ENCODER_SCRYPT_CPU_COST = "encoder.spring.scrypt.cpuCost";
    String SPRING_ENCODER_SCRYPT_MEMORY_COST = "encoder.spring.scrypt.memoryCost";
    String SPRING_ENCODER_SCRYPT_PARALLELIZATION = "encoder.spring.scrypt.parallelization";
    String SPRING_ENCODER_SCRYPT_KEY_LENGTH = "encoder.spring.scrypt.keyLength";
    String SPRING_ENCODER_SCRYPT_SALT_LENGTH = "encoder.spring.scrypt.saltLength";
    String SPRING_ENCODER_STANDARD_SECRET = "encoder.spring.standard.secret";

    String SHIRO_ENCODER = "encoder.shiro";
    String SHIRO_ENCODER_GENERATE_PUBLIC_SALT = "encoder.shiro.generatePublicSalt";
    String SHIRO_ENCODER_HASH_ALGORITHM_NAME = "encoder.shiro.hashAlgorithmName";
    String SHIRO_ENCODER_HASH_ITERATIONS = "encoder.shiro.hashIterations";
    String SHIRO_ENCODER_PRIVATE_SALT = "encoder.shiro.privateSalt";
}
