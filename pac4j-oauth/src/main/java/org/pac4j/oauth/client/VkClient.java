package org.pac4j.oauth.client;

import com.github.scribejava.apis.VkontakteApi;
import org.pac4j.oauth.profile.vk.VkConfiguration;
import org.pac4j.oauth.profile.vk.VkProfile;
import org.pac4j.oauth.profile.vk.VkProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in Vk.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user
 * by using the {@link #setScope(String)} method. By default, the <i>scope</i>
 * is : <code>PERMISSIONS</code>.</p>
 * <p>It returns a {@link VkProfile}.</p>
 * <p>More information at https://vk.com/dev/users.get</p>
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 *
 */
public class VkClient extends OAuth20Client {

    public VkClient() {
        configuration = new VkConfiguration();
    }

    public VkClient(final String key, final String secret) {
        configuration = new VkConfiguration();
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit() {
        configuration.setApi(VkontakteApi.instance());
        configuration.setProfileDefinition(new VkProfileDefinition());

        super.internalInit();
    }

    @Override
    public VkConfiguration getConfiguration() {
        return (VkConfiguration) configuration;
    }

    public String getScope() {
        return this.configuration.getScope();
    }

    public void setScope(final String scope) {
        this.configuration.setScope(scope);
    }

    public String getFields() {
        return getConfiguration().getFields();
    }

    public void setFields(final String fields) {
        getConfiguration().setFields(fields);
    }
}
