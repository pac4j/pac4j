/*
 * Copyright 2012 - 2015 pac4j organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.pac4j.cas.client.rest;

import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.util.CommonHelper;

/**
 * Direct client which receives credentials as form parameters and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestFormClient extends AbstractCasRestClient {

    private String casServerPrefixUrl;

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    public CasRestFormClient() {}

    public CasRestFormClient(final String casServerPrefixUrl) {
        this.casServerPrefixUrl = casServerPrefixUrl;
    }

    public CasRestFormClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    public CasRestFormClient(final String casServerPrefixUrl, final String usernameParameter, final String passwordParameter) {
        this.casServerPrefixUrl = casServerPrefixUrl;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    public CasRestFormClient(final Authenticator authenticator, final String usernameParameter, final String passwordParameter) {
        setAuthenticator(authenticator);
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("usernameParameter", this.usernameParameter);
        CommonHelper.assertNotBlank("passwordParameter", this.passwordParameter);
        if (CommonHelper.isNotBlank(this.casServerPrefixUrl)) {
            setAuthenticator(new CasRestAuthenticator(this.casServerPrefixUrl));
        }
        setCredentialsExtractor(new FormExtractor(this.usernameParameter, this.passwordParameter, getName()));
        super.internalInit(context);
        assertAuthenticatorTypes(CasRestAuthenticator.class);
    }

    public String getCasServerPrefixUrl() {
        return casServerPrefixUrl;
    }

    public void setCasServerPrefixUrl(String casServerPrefixUrl) {
        this.casServerPrefixUrl = casServerPrefixUrl;
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    @Deprecated
    public String getUsername() {
        return usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }

    @Deprecated
    public String getPassword() {
        return passwordParameter;
    }

    public void setPasswordParameter(String passwordParameter) {
        this.passwordParameter = passwordParameter;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "usernameParameter", this.usernameParameter,
                "passwordParameter", this.passwordParameter, "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
