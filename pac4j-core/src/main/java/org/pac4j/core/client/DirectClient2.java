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
package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.Extractor;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

/**
 * New direct client type using the {@link Extractor}, {@link Authenticator} and {@link ProfileCreator} concepts.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClient2<C extends Credentials, U extends CommonProfile> extends DirectClient<C, U> {

    private Extractor<C> extractor;

    private Authenticator<C> authenticator;

    private ProfileCreator<C, U> profileCreator = AuthenticatorProfileCreator.INSTANCE;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("extractor", this.extractor);
        CommonHelper.assertNotNull("authenticator", this.authenticator);
        CommonHelper.assertNotNull("profileCreator", this.profileCreator);
        if (authenticator instanceof InitializableWebObject) {
            ((InitializableWebObject) this.authenticator).init(context);
        }
    }

    @Override
    public C getCredentials(final WebContext context) throws RequiresHttpAction {
        init(context);
        try {
            final C credentials = this.extractor.extract(context);
            if (credentials == null) {
                return null;
            }
            this.authenticator.validate(credentials);
            return credentials;
        } catch (CredentialsException e) {
            logger.error("Failed to retrieve or validate credentials", e);
            return null;
        }
    }

    @Override
    protected U retrieveUserProfile(final C credentials, final WebContext context) {
        final U profile = this.profileCreator.create(credentials);
        logger.debug("profile: {}", profile);
        return profile;
    }

    protected void assertAuthenticatorTypes(final Class<? extends Authenticator>... classes) {
        if (this.authenticator != null && classes != null) {
            for (final Class<? extends Authenticator> clazz : classes) {
                Class<? extends Authenticator> authClazz = this.authenticator.getClass();
                if (LocalCachingAuthenticator.class.isAssignableFrom(authClazz)) {
                    authClazz = ((LocalCachingAuthenticator) this.authenticator).getDelegate().getClass();
                }
                if (!clazz.isAssignableFrom(authClazz)) {
                    throw new TechnicalException("Unsupported authenticator type: " + authClazz);
                }
            }
        }
    }


    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "extractor", this.extractor,
                "authenticator", this.authenticator, "profileCreator", this.profileCreator);
    }

    public Extractor<C> getExtractor() {
        return extractor;
    }

    public void setExtractor(final Extractor<C> extractor) {
        if (this.extractor == null) {
            this.extractor = extractor;
        }
    }

    public Authenticator<C> getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(final Authenticator<C> authenticator) {
        if (this.authenticator == null) {
            this.authenticator = authenticator;
        }
    }

    public ProfileCreator<C, U> getProfileCreator() {
        return profileCreator;
    }

    public void setProfileCreator(final ProfileCreator<C, U> profileCreator) {
        if (this.profileCreator == null || this.profileCreator == AuthenticatorProfileCreator.INSTANCE) {
            this.profileCreator = profileCreator;
        }
    }
}
