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

    private FacebookProperties facebook = new FacebookProperties();

    private TwitterProperties twitter = new TwitterProperties();

    private GitHubProperties gitHub = new GitHubProperties();

    private GoogleProperties google = new GoogleProperties();

    private OAuth20Properties oauth2 = new OAuth20Properties();

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
}
