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
package org.pac4j.core.exception;

/**
 * This class represents the root technical exception for the library.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class TechnicalException extends RuntimeException {
    
    private static final long serialVersionUID = 536639932593211210L;
    
    public TechnicalException(final String message) {
        super(message);
    }
    
    public TechnicalException(final Throwable t) {
        super(t);
    }
    
    public TechnicalException(final String message, final Throwable t) {
        super(message, t);
    }
}
