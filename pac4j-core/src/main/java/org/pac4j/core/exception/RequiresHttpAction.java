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

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * This exception is thrown when an additionnal HTTP action (redirect, basic auth...) is required.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class RequiresHttpAction extends Exception {
    
    private static final long serialVersionUID = -3959659239684160075L;
    
    protected int code;
    
    protected RequiresHttpAction(final String message, final int code) {
        super(message);
        this.code = code;
    }
    
    /**
     * Build a redirection.
     * 
     * @param message
     * @param context
     * @param url
     * @return an HTTP redirection
     */
    public static RequiresHttpAction redirect(final String message, final WebContext context, final String url) {
        context.setResponseHeader(HttpConstants.LOCATION_HEADER, url);
        context.setResponseStatus(HttpConstants.TEMP_REDIRECT);
        return new RequiresHttpAction(message, HttpConstants.TEMP_REDIRECT);
    }
    
    /**
     * Build an HTTP Ok.
     * 
     * @param message
     * @param context
     * @return an HTTP ok
     */
    public static RequiresHttpAction ok(final String message, final WebContext context) {
        return ok(message, context, "");
    }
    
    /**
     * Build an HTTP Ok.
     * 
     * @param message
     * @param context
     * @param content
     * @return an HTTP ok
     */
    public static RequiresHttpAction ok(final String message, final WebContext context, String content) {
        context.setResponseStatus(HttpConstants.OK);
        context.writeResponseContent(content);
        return new RequiresHttpAction(message, HttpConstants.OK);
    }
    
    /**
     * Build a basic auth popup credentials.
     * 
     * @param message
     * @param context
     * @param realmName
     * @return a basic auth popup credentials
     */
    public static RequiresHttpAction unauthorized(final String message, final WebContext context, final String realmName) {
        if (CommonHelper.isNotBlank(realmName)) {
            context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");
        }
        context.setResponseStatus(HttpConstants.UNAUTHORIZED);
        return new RequiresHttpAction(message, HttpConstants.UNAUTHORIZED);
    }
    
    /**
     * Build a forbidden response.
     * 
     * @param message
     * @param context
     * @return a forbidden response
     */
    public static RequiresHttpAction forbidden(final String message, final WebContext context) {
        context.setResponseStatus(HttpConstants.FORBIDDEN);
        return new RequiresHttpAction(message, HttpConstants.FORBIDDEN);
    }
    
    /**
     * Return the HTTP code.
     * 
     * @return the HTTP code
     */
    public int getCode() {
        return this.code;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(RequiresHttpAction.class, "code", this.code);
    }
}
