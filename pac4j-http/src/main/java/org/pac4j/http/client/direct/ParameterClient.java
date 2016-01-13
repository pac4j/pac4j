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
package org.pac4j.http.client.direct;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.TokenCredentials;
import org.pac4j.http.credentials.authenticator.TokenAuthenticator;
import org.pac4j.http.credentials.extractor.ParameterExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

/**
 * <p>This class is the client to authenticate users directly based on a provided parameter (in a GET and/or POST request).</p>
 * <p>It returns a {@link org.pac4j.http.profile.HttpProfile}.</p>
 *
 * @see org.pac4j.http.profile.HttpProfile
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ParameterClient extends DirectHttpClient<TokenCredentials> {

    private String parameterName = "";

    private boolean supportGetRequest = false;

    private boolean supportPostRequest = true;

    public ParameterClient() {
    }

    public ParameterClient(final String parameterName, final TokenAuthenticator tokenAuthenticator) {
        this.parameterName = parameterName;
        setAuthenticator(tokenAuthenticator);
    }

    public ParameterClient(final String parameterName,
                           final TokenAuthenticator tokenAuthenticator,
                           final ProfileCreator profileCreator) {
        this.parameterName = parameterName;
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        extractor = new ParameterExtractor(this.parameterName, this.supportGetRequest, this.supportPostRequest, getName());
        super.internalInit(context);
        CommonHelper.assertNotBlank("parameterName", this.parameterName);
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public boolean isSupportGetRequest() {
        return supportGetRequest;
    }

    public void setSupportGetRequest(boolean supportGetRequest) {
        this.supportGetRequest = supportGetRequest;
    }

    public boolean isSupportPostRequest() {
        return supportPostRequest;
    }

    public void setSupportPostRequest(boolean supportPostRequest) {
        this.supportPostRequest = supportPostRequest;
    }
}
