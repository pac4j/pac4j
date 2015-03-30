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
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.XmlHelper;
import org.pac4j.oauth.profile.orcid.OrcidProfile;
import org.scribe.builder.api.OrcidApi20;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;
import org.scribe.tokens.OrcidToken;


/**
 * <p>This class is the OAuth client to authenticate users in ORCiD.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.orcid.OrcidProfile}.</p>
 * <p>More information at http://support.orcid.org/knowledgebase/articles/175079-tutorial-retrieve-data-from-an-orcid-record-with</p>
 *
 * @see org.pac4j.oauth.profile.orcid.OrcidProfile
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidClient extends BaseOAuth20Client<OrcidProfile> {

    protected static final String DEFAULT_SCOPE = "/orcid-profile/read-limited";

    protected String scope = DEFAULT_SCOPE;

    public OrcidClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
        setTokenAsHeader(true);
    }

    public OrcidClient() {
        setTokenAsHeader(true);
    }

    @Override
    protected boolean requiresStateParameter() {
        return false;
    }

    @Override
    protected boolean hasBeenCancelled(WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        final String errorDescription = context.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
        // user has denied permissions
        if ("access_denied".equals(error) && "User denied access".equals(errorDescription)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String getProfileUrl(final Token accessToken) {
        if (accessToken instanceof OrcidToken) {
            return String.format("https://api.orcid.org/v1.1/%s/orcid-profile",
                    ((OrcidToken) accessToken).getOrcid());
        } else {
            throw new OAuthException("Token in getProfileUrl is not an OrcidToken");
        }
    }

    @Override
    protected void internalInit() {
        super.internalInit();
        this.service = new ProxyOAuth20ServiceImpl(new OrcidApi20(), new OAuthConfig(this.key,
                this.secret,
                this.callbackUrl,
                SignatureType.Header,
                this.getScope(),
                null), this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, false, true);
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    @Override
    protected OrcidProfile extractUserProfile(String body) {
        OrcidProfile profile = new OrcidProfile();
        for(final String attribute : OAuthAttributesDefinitions.orcidDefinition.getAllAttributes()) {
            profile.addAttribute(attribute, XmlHelper.get(body, attribute));
        }
        return profile;
    }

    @Override
    protected OrcidClient newClient() {
        final OrcidClient newClient = new OrcidClient();
        newClient.setScope(this.scope);
        return newClient;
    }

}
