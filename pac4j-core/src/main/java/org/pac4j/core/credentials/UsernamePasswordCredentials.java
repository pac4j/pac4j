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
package org.pac4j.core.credentials;

import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a username and a password credentials
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class UsernamePasswordCredentials extends Credentials {

    private static final long serialVersionUID = -7229878989627796565L;

    private String username;

    private String password;

    public UsernamePasswordCredentials(final String username, final String password, final String clientName) {
        this.username = username;
        this.password = password;
        setClientName(clientName);
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return !(password != null ? !password.equals(that.password) : that.password != null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "username", this.username, "password", "[PROTECTED]",
                "clientName", getClientName());
    }
}
