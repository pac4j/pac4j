package org.pac4j.oauth.client;

import com.github.scribejava.apis.OdnoklassnikiApi;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.ok.OkConfiguration;
import org.pac4j.oauth.profile.ok.OkProfileDefinition;

/**
 * <p>OkClient class.</p>
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public final class OkClient extends OAuth20Client {

    /**
     * <p>Constructor for OkClient.</p>
     */
    public OkClient() {
        configuration = new OkConfiguration();
    }

    /**
     * <p>Constructor for OkClient.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param secret a {@link java.lang.String} object
     * @param publicKey a {@link java.lang.String} object
     */
    public OkClient(final String key, final String secret,final String publicKey) {
        configuration = new OkConfiguration();
        setKey(key);
        setSecret(secret);
        setPublicKey(publicKey);
    }

    /** {@inheritDoc} */
    @Override
    public OkConfiguration getConfiguration() {
        return (OkConfiguration) configuration;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("publicKey", getConfiguration().getPublicKey());
        configuration.setApi(OdnoklassnikiApi.instance());
        configuration.setProfileDefinition(new OkProfileDefinition());

        super.internalInit(forceReinit);
    }

    /**
     * <p>getPublicKey.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPublicKey() {
        return getConfiguration().getPublicKey();
    }

    /**
     * <p>setPublicKey.</p>
     *
     * @param publicKey a {@link java.lang.String} object
     */
    public void setPublicKey(final String publicKey) {
        getConfiguration().setPublicKey(publicKey);
    }
}
