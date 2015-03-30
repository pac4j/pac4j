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

import org.pac4j.core.client.BaseClient;
import org.pac4j.core.client.Mechanism;
import org.pac4j.core.client.ClientIT;
import org.pac4j.core.util.TestsHelper;

/**
 * This class is the generic test case for OAuth client.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
@SuppressWarnings("rawtypes")
public abstract class OAuthClientIT extends ClientIT {
    
    private static final int CONNECT_TIMEOUT = 135;
    
    private static final int READ_TIMEOUT = 2896;
    
    private static final String PROXY_HOST = "proxyHost";
    
    private static final int PROXY_PORT = 12345;
    
    public void testMissingKey() {
        final BaseOAuthClient client = (BaseOAuthClient) getClient();
        client.setKey(null);
        TestsHelper.initShouldFail(client, "key cannot be blank");
    }
    
    public void testMissingSecret() {
        final BaseOAuthClient client = (BaseOAuthClient) getClient();
        client.setSecret(null);
        TestsHelper.initShouldFail(client, "secret cannot be blank");
    }
    
    @Override
    protected BaseClient internalTestClone(final BaseClient oldBaseClient) {
        BaseOAuthClient oldClient = (BaseOAuthClient) oldBaseClient;
        oldClient.setKey(KEY);
        oldClient.setSecret(SECRET);
        oldClient.setConnectTimeout(CONNECT_TIMEOUT);
        oldClient.setReadTimeout(READ_TIMEOUT);
        oldClient.setProxyHost(PROXY_HOST);
        oldClient.setProxyPort(PROXY_PORT);
        BaseOAuthClient client = (BaseOAuthClient) super.internalTestClone(oldClient);
        assertEquals(oldClient.getKey(), client.getKey());
        assertEquals(oldClient.getSecret(), client.getSecret());
        assertEquals(oldClient.getConnectTimeout(), client.getConnectTimeout());
        assertEquals(oldClient.getReadTimeout(), client.getReadTimeout());
        assertEquals(oldClient.getProxyHost(), client.getProxyHost());
        assertEquals(oldClient.getProxyPort(), client.getProxyPort());
        return client;
    }
    
    public void testClone() {
        internalTestClone((BaseClient) getClient());
    }
    
    @Override
    protected Mechanism getMechanism() {
        return Mechanism.OAUTH_PROTOCOL;
    }
}
