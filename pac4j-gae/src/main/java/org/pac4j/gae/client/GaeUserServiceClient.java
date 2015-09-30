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
package org.pac4j.gae.client;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.ClientType;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.gae.credentials.GaeUserCredentials;
import org.pac4j.gae.profile.GaeUserServiceAttributesDefinition;
import org.pac4j.gae.profile.GaeUserServiceProfile;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * <p>This class is the OpenID client to authenticate users with UserService on App Engine</p>
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserServiceClient extends IndirectClient<GaeUserCredentials, GaeUserServiceProfile> {
	UserService service;
	String authDomain = null;
	public GaeUserServiceClient() {
		setName("GaeUserServiceClient");
	}
	@Override
	protected IndirectClient<GaeUserCredentials, GaeUserServiceProfile> newClient() {
		GaeUserServiceClient gaeUserServiceClient = new GaeUserServiceClient();
		gaeUserServiceClient.setAuthDomain(authDomain);
		return gaeUserServiceClient;
	}

	@Override
	protected boolean isDirectRedirection() {
		return true;
	}

	@Override
	protected RedirectAction retrieveRedirectAction(WebContext context) {
		String destinationUrl = getCallbackUrl();
		String loginUrl = authDomain == null ?  service.createLoginURL(destinationUrl) : service.createLoginURL(destinationUrl, authDomain);
		return RedirectAction.redirect(loginUrl);
	}

	@Override
	protected GaeUserCredentials retrieveCredentials(WebContext context)
			throws RequiresHttpAction {
		GaeUserCredentials credentials = new GaeUserCredentials();
		credentials.setUser(service.getCurrentUser());
		return credentials;
	}

	@Override
	protected GaeUserServiceProfile retrieveUserProfile(GaeUserCredentials credentials, WebContext context) {
		User user = credentials.getUser();
		if (user != null) {
			GaeUserServiceProfile gaeUserProfile = new GaeUserServiceProfile();
			gaeUserProfile.setId(user.getEmail());
			gaeUserProfile.addAttribute(GaeUserServiceAttributesDefinition.EMAIL, user.getEmail());
			gaeUserProfile.addAttribute(GaeUserServiceAttributesDefinition.DISPLAYNAME, user.getNickname());
			if (service.isUserAdmin()) {
				gaeUserProfile.addRole(GaeUserServiceProfile.PAC4J_GAE_GLOBAL_ADMIN_ROLE);
			}
			return gaeUserProfile;
		}
		return null;
	}

	@Override
	public ClientType getClientType() {
		return ClientType.GAE_PROVIDER;
	}

	@Override
	protected void internalInit() {
		service = UserServiceFactory.getUserService();
		CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
	}
	
	/**
	 * Set the authDomain for connect to google apps for domain with the UserService
	 * @param authDomain the authentication domain
	 */
	public void setAuthDomain(String authDomain) {
		this.authDomain = authDomain;
	}
	
	public String getAuthDomain() {
		return authDomain;
	}

}
