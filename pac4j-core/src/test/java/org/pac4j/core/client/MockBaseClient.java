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
package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

/**
 * This is a mock client with settable name / direct redirection (for tests purpose).
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class MockBaseClient<C extends Credentials> extends BaseClient<C, CommonProfile> implements TestsConstants {
    
    private boolean isDirect = true;
    
    public MockBaseClient(final String name) {
        setName(name);
    }
    
    public MockBaseClient(final String name, final boolean isDirect) {
        setName(name);
        this.isDirect = isDirect;
    }
    
    @Override
    protected BaseClient<C, CommonProfile> newClient() {
        return new MockBaseClient<C>(getName());
    }
    
    @Override
    protected void internalInit() {
    }
    
    @Override
    protected CommonProfile retrieveUserProfile(final C credentials, final WebContext context) {
        return new CommonProfile();
    }
    
    @Override
    protected boolean isDirectRedirection() {
        return this.isDirect;
    }
    
    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        return RedirectAction.redirect(LOGIN_URL);
    }
    
    @Override
    protected C retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        return null;
    }
    
    @Override
    public Protocol getProtocol() {
        return null;
    }
}
