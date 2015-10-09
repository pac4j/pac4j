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

package org.pac4j.saml.exceptions;

import org.pac4j.core.exception.TechnicalException;

/**
 * Root exception for SAML Client.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAMLException extends TechnicalException {

    private static final long serialVersionUID = -2963580056603469743L;

    public SAMLException(final String message) {
        super(message);
    }

    public SAMLException(final Throwable t) {
        super(t);
    }

    public SAMLException(final String message, final Throwable t) {
        super(message, t);
    }
}
