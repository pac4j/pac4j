/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.provider.exception;

/**
 * This class represents an HTTP exception which can happen in network communication (with status code and message body).
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class HttpException extends RootException {
    
    private static final long serialVersionUID = 927766263661212605L;
    
    private final int code;
    
    private final String body;
    
    public HttpException(final int code, final String body) {
        this.code = code;
        this.body = body;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getBody() {
        return this.body;
    }
}
