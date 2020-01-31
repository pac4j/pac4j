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

    private final FacebookProperties facebook = new FacebookProperties();
    private final TwitterProperties twitter = new TwitterProperties();

    public TwitterProperties getTwitter() {
        return twitter;
    }

    public FacebookProperties getFacebook() {
        return facebook;
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
