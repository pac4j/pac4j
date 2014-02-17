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
package org.pac4j.http.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents a username and a password credentials
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class UsernamePasswordCredentials extends Credentials {
    
    private static final long serialVersionUID = -7229878989627796565L;
    
    private final String username;
    
    private final String password;
    
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
    public String toString() {
        return CommonHelper.toString(this.getClass(), "username", this.username, "password", "[PROTECTED]",
                                     "clientName", getClientName());
    }
}
