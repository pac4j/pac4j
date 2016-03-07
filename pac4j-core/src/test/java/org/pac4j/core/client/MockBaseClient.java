package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;

/**
 * This is a mock client with settable name / direct redirection (for tests purpose).
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class MockBaseClient<C extends Credentials> extends IndirectClient<C, CommonProfile> implements TestsConstants {
    
    private boolean isDirect = true;
    
    public MockBaseClient(final String name) {
        setName(name);
    }
    
    public MockBaseClient(final String name, final boolean isDirect) {
        setName(name);
        this.isDirect = isDirect;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("callbackUrl", getCallbackUrl());
    }
    
    @Override
    protected CommonProfile retrieveUserProfile(final C credentials, final WebContext context) {
        return new CommonProfile();
    }
    
    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        return RedirectAction.redirect(LOGIN_URL);
    }
    
    @Override
    protected C retrieveCredentials(final WebContext context) throws RequiresHttpAction {
        return null;
    }
}
