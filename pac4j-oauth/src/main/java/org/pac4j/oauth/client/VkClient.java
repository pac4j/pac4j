/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.vk.VkProfile;
import org.scribe.builder.api.VkApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * <p>This class is the OAuth client to authenticate users in Vk.</p>
 * <p>The <i>scope</i> can be defined to require specific permissions from the user
 * by using the {@link #setScope(String)} method. By default, the <i>scope</i>
 * is : <code>PERMISSIONS</code>.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.vk.VkProfile}.</p>
 * <p>More information at https://vk.com/dev/users.get</p>
 * 
 * @see org.pac4j.oauth.profile.vk.VkProfile
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
	protected VkClient newClient() {
		VkClient client = new VkClient();
		client.setScope(this.scope);
		return client;
	}

	@Override
	protected void internalInit() {
		super.internalInit();
		this.service = new ProxyOAuth20ServiceImpl(new VkApi(),
				new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, this.scope, null), this.connectTimeout, this.readTimeout,
				this.proxyHost, this.proxyPort);
	}

	@Override
	protected String getProfileUrl(final Token accessToken) {
		String url = BASE_URL + "?fields=" + this.fields;
		return url;
	}

	@Override
	protected VkProfile extractUserProfile(final String body) {
		final VkProfile profile = new VkProfile();
		JsonNode json = JsonHelper.getFirstNode(body);
		if (json != null) {
			ArrayNode array = (ArrayNode) json.get("response");
			JsonNode userNode = array.get(0);
			profile.setId(JsonHelper.get(userNode, "uid"));
			for (final String attribute : OAuthAttributesDefinitions.vkDefinition.getAllAttributes()) {
				profile.addAttribute(attribute, JsonHelper.get(userNode, attribute));
			}
		}
		return profile;
	}

	@Override
	protected boolean requiresStateParameter() {
		return false;
	}

	@Override
	protected boolean hasBeenCancelled(final WebContext context) {
		return false;
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
