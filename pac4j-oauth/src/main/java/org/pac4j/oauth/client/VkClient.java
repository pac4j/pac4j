package org.pac4j.oauth.client;

import com.github.scribejava.apis.VkontakteApi;
import org.pac4j.oauth.profile.vk.VkConfiguration;
import org.pac4j.oauth.profile.vk.VkProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in Vk.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user
 * by using the {@link #setScope(String)} method. By default, the <i>scope</i>
 * is : <code>PERMISSIONS</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.vk.VkProfile}.</p>
 * <p>More information at https://vk.com/dev/users.get</p>
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class VkClient extends OAuth20Client {

    /**
     * <p>Constructor for VkClient.</p>
     */
    public VkClient() {
        configuration = new VkConfiguration();
    }

    /**
     * <p>Constructor for VkClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public VkClient(final String key, final String secret) {
        configuration = new VkConfiguration();
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        configuration.setApi(VkontakteApi.instance());
        configuration.setProfileDefinition(new VkProfileDefinition());

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    public VkConfiguration getConfiguration() {
        return (VkConfiguration) configuration;
    }

    /**
     * <p>getScope.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return this.configuration.getScope();
    }

    /**
     * <p>setScope.</p>
     *
     * @param scope a {@link String} object
     */
    public void setScope(final String scope) {
        this.configuration.setScope(scope);
    }

    /**
     * <p>getFields.</p>
     *
     * @return a {@link String} object
     */
    public String getFields() {
        return getConfiguration().getFields();
    }

    /**
     * <p>setFields.</p>
     *
     * @param fields a {@link String} object
     */
    public void setFields(final String fields) {
        getConfiguration().setFields(fields);
    }
}
