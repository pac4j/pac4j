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
package org.pac4j.core.exception;

/**
 * This class represents an exception which happens when a basic authentication is required and which should be handled by the application.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class RequiresBasicAuthException extends CredentialsException {
    
    private static final long serialVersionUID = -6942192034499308108L;
    
    protected final String realmName;
    
    public RequiresBasicAuthException(final String message, final String realmName) {
        super(message);
        this.realmName = realmName;
    }
    
    public String getRealmName() {
        return this.realmName;
    }
}
