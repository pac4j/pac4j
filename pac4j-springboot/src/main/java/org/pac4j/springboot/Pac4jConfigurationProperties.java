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
}
