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

import org.pac4j.core.client.DirectClient2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.TokenAuthenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.http.credentials.extractor.IpExtractor;

/**
 * <p>This class is the client to authenticate users directly based on their IP address.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class IpClient extends DirectClient2<TokenCredentials, CommonProfile> {

    public IpClient() {}

    public IpClient(final Authenticator tokenAuthenticator) {
        setAuthenticator(tokenAuthenticator);
    }

    public IpClient(final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        setExtractor(new IpExtractor(getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(TokenAuthenticator.class);
    }
}
