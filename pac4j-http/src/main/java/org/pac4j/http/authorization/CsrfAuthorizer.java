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
package org.pac4j.http.authorization;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * Authorizer that checks CSRF tokens.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class CsrfAuthorizer <U extends UserProfile> implements Authorizer<U> {

    private String parameterName = Pac4jConstants.CSRK_TOKEN;

    private String headerName = Pac4jConstants.CSRK_TOKEN;

    public boolean isAuthorized(final WebContext context, final U profile) {
        final String parameterToken = context.getRequestParameter(parameterName);
        final String headerToken = context.getRequestHeader(headerName);
        final String sessionToken = (String) context.getSessionAttribute(Pac4jConstants.CSRK_TOKEN);
        return CommonHelper.areEquals(parameterToken, sessionToken) || CommonHelper.areEquals(headerToken, sessionToken);
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
