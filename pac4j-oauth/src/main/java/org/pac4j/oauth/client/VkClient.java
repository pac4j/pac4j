package org.pac4j.oauth.client;

import com.github.scribejava.apis.VkontakteApi;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.vk.VkProfile;
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
 *
 */
public class VkClient extends OAuth20Client<VkProfile> {

	public final static String DEFAULT_FIELDS = "sex,bdate,photo_50,photo_100,photo_200_orig,photo_200,photo_400_orig,photo_max,photo_max_orig,online,online_mobile,lists,domain,has_mobile,contacts,connections,site,education,can_post,can_see_all_posts,can_see_audio,can_write_private_message,status,common_count,relation,relatives";

	protected String fields = DEFAULT_FIELDS;

	public final static String DEFAULT_SCOPE = "PERMISSIONS";

	protected String scope = DEFAULT_SCOPE;

	public VkClient() {
	}

	public VkClient(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected void clientInit(final WebContext context) {
		configuration.setApi(VkontakteApi.instance());
		configuration.setProfileDefinition(new VkProfileDefinition());
		configuration.setScope(this.scope);
		setConfiguration(configuration);

		super.clientInit(context);
	}

	public String getScope() {
		return this.scope;
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
