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
 * This class represents an exception which can happen during HTTP communication (with status code and message body).
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class HttpCommunicationException extends CommunicationException {
    
    private static final long serialVersionUID = -7972641539531738263L;
    
    private final int code;
    
    private final String body;
    
    public HttpCommunicationException(final int code, final String body) {
        super("Failed to retrieve data / failed code : " + code + " and body : " + body);
        this.code = code;
        this.body = body;
    }
    
    public HttpCommunicationException(final Throwable t) {
        super(t);
        this.code = 0;
        this.body = null;
    }
    
    public HttpCommunicationException(final String message) {
        super(message);
        this.code = 0;
        this.body = null;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getBody() {
        return this.body;
    }
}
