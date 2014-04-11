/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.openid.client;

import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.credentials.OpenIdCredentials;
import org.pac4j.openid.profile.OpenIdAttributesDefinitions;
import org.pac4j.openid.profile.google.GoogleOpenIdAttributesDefinition;
import org.pac4j.openid.profile.google.GoogleOpenIdProfile;

/**
 * This class is the OpenID client to authenticate users with their google account.
 * <p />
 * It returns a {@link org.pac4j.openid.profile.google.GoogleOpenIdProfile}.
 * 
 * @see org.pac4j.openid.profile.google.GoogleOpenIdProfile
 * @author Stephane Gleizes
 * @since 1.4.1
 */
public class GoogleOpenIdClient extends BaseOpenIdClient<GoogleOpenIdProfile> {

    public static final String GOOGLE_GENERIC_USER_IDENTIFIER = "https://www.google.com/accounts/o8/id";

    @Override
    protected void internalInit() {
        super.internalInit();
    }

    @Override
    protected BaseClient<OpenIdCredentials, GoogleOpenIdProfile> newClient() {
        return new GoogleOpenIdClient();
    }

    @Override
    protected String getUser(final WebContext context) {
        return GOOGLE_GENERIC_USER_IDENTIFIER;
    }

    @Override
    protected FetchRequest getFetchRequest() throws MessageException {
        final FetchRequest fetchRequest = FetchRequest.createFetchRequest();
        fetchRequest.addAttribute(GoogleOpenIdAttributesDefinition.COUNTRY, "http://axschema.org/contact/country/home",
                true);
        fetchRequest.addAttribute(GoogleOpenIdAttributesDefinition.EMAIL, "http://axschema.org/contact/email", true);
        fetchRequest.addAttribute(GoogleOpenIdAttributesDefinition.FIRSTNAME, "http://axschema.org/namePerson/first",
                true);
        fetchRequest.addAttribute(GoogleOpenIdAttributesDefinition.LANGUAGE, "http://axschema.org/pref/language", true);
        fetchRequest.addAttribute(GoogleOpenIdAttributesDefinition.LASTNAME, "http://axschema.org/namePerson/last",
                true);
        logger.debug("fetchRequest: {}", fetchRequest);
        return fetchRequest;
    }

    @Override
    protected GoogleOpenIdProfile createProfile(final AuthSuccess authSuccess) throws MessageException {
        final GoogleOpenIdProfile profile = new GoogleOpenIdProfile();

        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
            final FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
            for (final String name : OpenIdAttributesDefinitions.googleOpenIdDefinition.getAllAttributes()) {
                profile.addAttribute(name, fetchResp.getAttributeValue(name));
            }
        }
        return profile;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName());
    }
}
