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
 * This exception is thrown when an additionnal HTTP action (redirect, basic auth...) is required.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class RequiresHttpAction extends Exception {
    
    private static final long serialVersionUID = -1281366630912081625L;
    
    public RequiresHttpAction(final String message) {
        super(message);
    }
    
    public RequiresHttpAction(final Throwable t) {
        super(t);
    }
    
    public RequiresHttpAction(final String message, final Throwable t) {
        super(message, t);
    }
}
