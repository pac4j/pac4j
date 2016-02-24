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
package org.pac4j.oidc.credentials;

import org.pac4j.core.credentials.Credentials;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import org.pac4j.core.util.CommonHelper;

/**
 * Credentials containing the authorization code sent by the OpenID Connect server.
 * 
 * @author Michael Remond
 * @since 1.7.0
 */
public class OidcCredentials extends Credentials {

    private static final long serialVersionUID = 6772331801527223938L;

    private AuthorizationCode code;

    public OidcCredentials(final AuthorizationCode code, final String clientName) {
        this.code = code;
        this.setClientName(clientName);
    }

    public AuthorizationCode getCode() {
        return this.code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OidcCredentials that = (OidcCredentials) o;
        return !(code != null ? !code.equals(that.code) : that.code != null);

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "code", this.code, "clientName", getClientName());
    }
}
