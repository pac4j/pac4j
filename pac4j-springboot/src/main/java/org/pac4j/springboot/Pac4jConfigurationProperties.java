package org.pac4j.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * This is {@link Pac4jConfigurationProperties}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@ConfigurationProperties(prefix = Pac4jConfigurationProperties.PREFIX, ignoreUnknownFields = false)
public class Pac4jConfigurationProperties {
    public static final String PREFIX = "pac4j";

    private Saml2Properties saml2 = new Saml2Properties();

    private CasProperties cas = new CasProperties();

    private FacebookProperties facebook = new FacebookProperties();

    private TwitterProperties twitter = new TwitterProperties();

    private GitHubProperties gitHub = new GitHubProperties();

    private GoogleProperties google = new GoogleProperties();

    private OAuth20Properties oauth2 = new OAuth20Properties();

    private OidcProperties oidc = new OidcProperties();

    public Saml2Properties getSaml2() {
        return saml2;
    }

    public void setSaml2(final Saml2Properties saml2) {
        this.saml2 = saml2;
    }

    public CasProperties getCas() {
        return cas;
    }

    public void setCas(final CasProperties cas) {
        this.cas = cas;
    }

    public OidcProperties getOidc() {
        return oidc;
    }

    public void setOidc(final OidcProperties oidc) {
        this.oidc = oidc;
    }

    public OAuth20Properties getOauth2() {
        return oauth2;
    }

    public void setOauth2(final OAuth20Properties oauth2) {
        this.oauth2 = oauth2;
    }

    public GoogleProperties getGoogle() {
        return google;
    }

    public void setGoogle(final GoogleProperties google) {
        this.google = google;
    }

    public GitHubProperties getGitHub() {
        return gitHub;
    }

    public void setGitHub(final GitHubProperties gitHub) {
        this.gitHub = gitHub;
    }

    public TwitterProperties getTwitter() {
        return twitter;
    }

    public void setTwitter(final TwitterProperties twitter) {
        this.twitter = twitter;
    }

    public FacebookProperties getFacebook() {
        return facebook;
    }

    public void setFacebook(final FacebookProperties facebook) {
        this.facebook = facebook;
    }

    public static class Saml2Properties {
        private String keystorePassword;

        private String privateKeyPassword;

        private String keystorePath;

        private String identityProviderMetadataPath;

        private String maximumAuthenticationLifetime;

        private String serviceProviderEntityId;

        private String serviceProviderMetadataPath;

        private String authnRequestBindingType;

        private String keystoreAlias;

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(final String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }

        public String getPrivateKeyPassword() {
            return privateKeyPassword;
        }

        public void setPrivateKeyPassword(final String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public void setKeystorePath(final String keystorePath) {
            this.keystorePath = keystorePath;
        }

        public String getIdentityProviderMetadataPath() {
            return identityProviderMetadataPath;
        }

        public void setIdentityProviderMetadataPath(final String identityProviderMetadataPath) {
            this.identityProviderMetadataPath = identityProviderMetadataPath;
        }

        public String getMaximumAuthenticationLifetime() {
            return maximumAuthenticationLifetime;
        }

        public void setMaximumAuthenticationLifetime(final String maximumAuthenticationLifetime) {
            this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
        }

        public String getServiceProviderEntityId() {
            return serviceProviderEntityId;
        }

        public void setServiceProviderEntityId(final String serviceProviderEntityId) {
            this.serviceProviderEntityId = serviceProviderEntityId;
        }

        public String getServiceProviderMetadataPath() {
            return serviceProviderMetadataPath;
        }

        public void setServiceProviderMetadataPath(final String serviceProviderMetadataPath) {
            this.serviceProviderMetadataPath = serviceProviderMetadataPath;
        }

        public String getAuthnRequestBindingType() {
            return authnRequestBindingType;
        }

        public void setAuthnRequestBindingType(final String authnRequestBindingType) {
            this.authnRequestBindingType = authnRequestBindingType;
        }

        public String getKeystoreAlias() {
            return keystoreAlias;
        }

        public void setKeystoreAlias(final String keystoreAlias) {
            this.keystoreAlias = keystoreAlias;
        }
    }

    public static class TwitterProperties {
        private String id;

        private String secret;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }
    }

    public static class GitHubProperties {
        private String id;

        private String secret;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }
    }

    public static class GoogleProperties {
        private String id;

        private String scope;

        private String secret;

        public String getScope() {
            return scope;
        }

        public void setScope(final String scope) {
            this.scope = scope;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }
    }

    public static class FacebookProperties {
        private String id;

        private String secret;

        private String scope;

        private String fields;

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(final String scope) {
            this.scope = scope;
        }

        public String getFields() {
            return fields;
        }

        public void setFields(final String fields) {
            this.fields = fields;
        }
    }

    public static class OAuth20Properties {
        private String id;

        private String secret;

        private String authUrl;

        private String tokenUrl;

        private String profileUrl;

        private String profilePath;

        private String profileId;

        private String scope;

        private String withState;

        private String clientAuthenticationMethod;

        public String getAuthUrl() {
            return authUrl;
        }

        public void setAuthUrl(final String authUrl) {
            this.authUrl = authUrl;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }

        public void setTokenUrl(final String tokenUrl) {
            this.tokenUrl = tokenUrl;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(final String profileUrl) {
            this.profileUrl = profileUrl;
        }

        public String getProfilePath() {
            return profilePath;
        }

        public void setProfilePath(final String profilePath) {
            this.profilePath = profilePath;
        }

        public String getProfileId() {
            return profileId;
        }

        public void setProfileId(final String profileId) {
            this.profileId = profileId;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(final String scope) {
            this.scope = scope;
        }

        public String getWithState() {
            return withState;
        }

        public void setWithState(final String withState) {
            this.withState = withState;
        }

        public String getClientAuthenticationMethod() {
            return clientAuthenticationMethod;
        }

        public void setClientAuthenticationMethod(final String clientAuthenticationMethod) {
            this.clientAuthenticationMethod = clientAuthenticationMethod;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }
    }

    public static class OidcProperties {
        private String id;

        private String secret;

        private String type;

        private String azureTenant;

        private String scope;

        private String discoveryUri;

        private String useNonce;

        private String preferredJwsAlgorithm;

        private String maxClockSkew;

        private String clientAuthenticationMethod;

        private String customParamKey;

        private String customParamValue;

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(final String secret) {
            this.secret = secret;
        }

        public String getAzureTenant() {
            return azureTenant;
        }

        public void setAzureTenant(final String azureTenant) {
            this.azureTenant = azureTenant;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(final String scope) {
            this.scope = scope;
        }

        public String getDiscoveryUri() {
            return discoveryUri;
        }

        public void setDiscoveryUri(final String discoveryUri) {
            this.discoveryUri = discoveryUri;
        }

        public String getUseNonce() {
            return useNonce;
        }

        public void setUseNonce(final String useNonce) {
            this.useNonce = useNonce;
        }

        public String getPreferredJwsAlgorithm() {
            return preferredJwsAlgorithm;
        }

        public void setPreferredJwsAlgorithm(final String preferredJwsAlgorithm) {
            this.preferredJwsAlgorithm = preferredJwsAlgorithm;
        }

        public String getMaxClockSkew() {
            return maxClockSkew;
        }

        public void setMaxClockSkew(final String maxClockSkew) {
            this.maxClockSkew = maxClockSkew;
        }

        public String getClientAuthenticationMethod() {
            return clientAuthenticationMethod;
        }

        public void setClientAuthenticationMethod(final String clientAuthenticationMethod) {
            this.clientAuthenticationMethod = clientAuthenticationMethod;
        }

        public String getCustomParamKey() {
            return customParamKey;
        }

        public void setCustomParamKey(final String customParamKey) {
            this.customParamKey = customParamKey;
        }

        public String getCustomParamValue() {
            return customParamValue;
        }

        public void setCustomParamValue(final String customParamValue) {
            this.customParamValue = customParamValue;
        }
    }

    public static class CasProperties {
        private String loginUrl;

        private String protocol;

        public String getLoginUrl() {
            return loginUrl;
        }

        public void setLoginUrl(final String loginUrl) {
            this.loginUrl = loginUrl;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(final String protocol) {
            this.protocol = protocol;
        }
    }
}
