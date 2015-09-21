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
package org.pac4j.http.credentials;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.pac4j.core.util.CommonHelper;

/**
 * This credentials represents a token.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
public class TokenCredentials extends HttpCredentials {

    private static final long serialVersionUID = -4270718634364817595L;

    private String token;

    public TokenCredentials(String token, final String clientName) {
        this.token = token;
        setClientName(clientName);
    }

    public String getToken() {
        return this.token;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "token", this.token, "clientName", getClientName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenCredentials that = (TokenCredentials) o;
        final EqualsBuilder builder = new EqualsBuilder();
        return builder
                .append(this.token, that.token)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31)
                .append(this.token)
                .toHashCode();
    }

    @Override
    public void clear() {
        this.token = null;
        this.setClientName(null);
        this.setUserProfile(null);
    }
}
