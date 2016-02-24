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
 * Common constants.
 *
 * @author Jerome Leleu
 * @since 1.6.0
 */
public interface Pac4jConstants {

    /* Original requested url to save before redirect to Identity Provider */
    String REQUESTED_URL = "pac4jRequestedUrl";

    /* User Profile object saved in session */
    String USER_PROFILE = "pac4jUserProfile";

    /* CSRF token name saved in session */
    String CSRF_TOKEN = "pac4jCsrfToken";

    /* Session ID */
    String SESSION_ID = "pac4jSessionId";

    /* Client name configuration parameter */
    String CLIENT_NAME = "clientName";

    /* An AJAX parameter name to dynamically set a HTTP request as an AJAX one. */
    String IS_AJAX_REQUEST = "is_ajax_request";

    /* The name of an authorizer */
    String AUTHORIZER_NAME = "authorizerName";

    /* The default url parameter */
    String DEFAULT_URL = "defaultUrl";

    /* The default url, the root path */
    String DEFAULT_URL_VALUE = "/";

    /* The url parameter */
    String URL = "url";

    /* The element (client or authorizer) separator */
    String ELEMENT_SEPRATOR = ",";

    /* The logout pattern for url */
    String LOGOUT_URL_PATTERN = "logoutUrlPattern";

    /* The default value for the logout url pattern, meaning only relative urls are allowed */
    String DEFAULT_LOGOUT_URL_PATTERN_VALUE = "/.*";

    /* The config factory parameter */
    String CONFIG_FACTORY = "configFactory";

    /* Matcher name configuration parameter */
    String MATCHER_NAME = "matcherName";

    String USERNAME = "username";

    String PASSWORD = "password";
}
