package org.pac4j.oauth.profile.ok;

import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Ok OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public class OkConfiguration extends OAuth20Configuration {

    /**
     * Public key (required as well as application key by API on ok.ru)
     */
    private String publicKey;

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }
}
