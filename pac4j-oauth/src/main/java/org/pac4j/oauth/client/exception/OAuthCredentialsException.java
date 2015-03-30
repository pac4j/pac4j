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
package org.pac4j.oauth.client.exception;

import java.util.HashMap;
import java.util.Map;

import org.pac4j.core.exception.CredentialsException;

/**
 * This class represents an exception occuring during OAuth credentials retrieval.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuthCredentialsException extends CredentialsException {
    
    private static final long serialVersionUID = -3540979749535811079L;
    
    public static final String ERROR = "error";
    
    public static final String ERROR_REASON = "error_reason";
    
    public static final String ERROR_DESCRIPTION = "error_description";
    
    private static final String ERROR_URI = "error_uri";
    
    public static final String[] ERROR_NAMES = {
        ERROR, ERROR_REASON, ERROR_DESCRIPTION, ERROR_URI
    };
    
    private final Map<String, String> errorMessages = new HashMap<String, String>();
    
    public OAuthCredentialsException(final String message) {
        super(message);
    }
    
    public void setErrorMessage(final String name, final String message) {
        this.errorMessages.put(name, message);
    }
    
    public Map<String, String> getErrorMessages() {
        return this.errorMessages;
    }
    
    public String getError() {
        return this.errorMessages.get(ERROR);
    }
    
    public String getErrorReason() {
        return this.errorMessages.get(ERROR_REASON);
    }
    
    public String getErrorDescription() {
        return this.errorMessages.get(ERROR_DESCRIPTION);
    }
    
    public String getErrorUri() {
        return this.errorMessages.get(ERROR_URI);
    }
}
