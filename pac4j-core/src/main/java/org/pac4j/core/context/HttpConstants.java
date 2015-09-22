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
package org.pac4j.core.context;

/**
 * Some HTTP constants.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface HttpConstants {

    int OK = 200;

    /**
     * @since 1.8
     */
    int CREATED = 201;

    int UNAUTHORIZED = 401;

    int FORBIDDEN = 403;

    int TEMP_REDIRECT = 302;

    int DEFAULT_PORT = 80;

    String LOCATION_HEADER = "Location";

    String AUTHORIZATION_HEADER = "Authorization";

    String AUTHENTICATE_HEADER = "WWW-Authenticate";

    String CONTENT_TYPE_HEADER = "Content-Type";
    
    String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    String AJAX_HEADER_VALUE = "XMLHttpRequest";

    String AJAX_HEADER_NAME = "X-Requested-With";
}
