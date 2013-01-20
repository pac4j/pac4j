/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.profile.CommonProfile;

/**
 * This is a mock client with settable type (for tests purpose).
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class MockBaseClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {
    
    public MockBaseClient(final String type) {
        setType(type);
    }
    
    public String getRedirectionUrl(final WebContext context) throws ClientException {
        return null;
    }
    
    @Override
    protected BaseClient<C, U> newClient() {
        return new MockBaseClient<C, U>(getType());
    }
    
    @Override
    protected void internalInit() {
    }
    
    public C getCredentials(final WebContext context) throws ClientException {
        return null;
    }
    
    public U getUserProfile(final C credentials) throws ClientException {
        return null;
    }
}
