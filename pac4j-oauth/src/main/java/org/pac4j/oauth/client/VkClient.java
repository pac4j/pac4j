package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.scribejava.apis.VkontakteApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.vk.VkProfile;

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
public class VkClient extends BaseOAuth20Client<VkProfile> {

	public final static String DEFAULT_FIELDS = "sex,bdate,photo_50,photo_100,photo_200_orig,photo_200,photo_400_orig,photo_max,photo_max_orig,online,online_mobile,lists,domain,has_mobile,contacts,connections,site,education,can_post,can_see_all_posts,can_see_audio,can_write_private_message,status,common_count,relation,relatives";

	protected String fields = DEFAULT_FIELDS;

	public final static String DEFAULT_SCOPE = "PERMISSIONS";

	protected String scope = DEFAULT_SCOPE;

	protected final static String BASE_URL = "https://api.vk.com/method/users.get";

	public VkClient() {
	}

	public VkClient(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected BaseApi<OAuth20Service> getApi() {
		return VkontakteApi.instance();
	}

	@Override
	protected String getOAuthScope() {
		return this.scope;
	}

	@Override
	protected String getProfileUrl(final OAuth2AccessToken accessToken) {
		String url = BASE_URL + "?fields=" + this.fields;
		return url;
	}

	@Override
	protected VkProfile extractUserProfile(final String body) throws HttpAction {
		final VkProfile profile = new VkProfile();
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			ArrayNode array = (ArrayNode) json.get("response");
			JsonNode userNode = array.get(0);
			profile.setId(JsonHelper.getElement(userNode, "uid"));
			for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
				profile.addAttribute(attribute, JsonHelper.getElement(userNode, attribute));
			}
		}
		return profile;
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

	public void setFields(String fields) {
		this.fields = fields;
	}
}
