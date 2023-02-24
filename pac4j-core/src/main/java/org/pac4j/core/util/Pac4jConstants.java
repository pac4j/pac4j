package org.pac4j.core.util;

/**
 * Common constants.
 *
 * @author Jerome Leleu
 * @since 1.6.0
 */
public interface Pac4jConstants {

    /* Original requested url to save before redirect to Identity Provider */
    /** Constant <code>REQUESTED_URL="pac4jRequestedUrl"</code> */
    String REQUESTED_URL = "pac4jRequestedUrl";

    /* User profiles object saved in session */
    /** Constant <code>USER_PROFILES="pac4jUserProfiles"</code> */
    String USER_PROFILES = "pac4jUserProfiles";

    /* CSRF token name saved in session */
    /** Constant <code>PREVIOUS_CSRF_TOKEN="pac4jPreviousCsrfToken"</code> */
    String PREVIOUS_CSRF_TOKEN = "pac4jPreviousCsrfToken";
    /** Constant <code>CSRF_TOKEN="pac4jCsrfToken"</code> */
    String CSRF_TOKEN = "pac4jCsrfToken";

    /* CSRF token expiration date name saved in session */
    /** Constant <code>CSRF_TOKEN_EXPIRATION_DATE="pac4jCsrfTokenExpirationDate"</code> */
    String CSRF_TOKEN_EXPIRATION_DATE = "pac4jCsrfTokenExpirationDate";

    /* Session ID */
    /** Constant <code>SESSION_ID="pac4jSessionId"</code> */
    String SESSION_ID = "pac4jSessionId";

    /* Client names configuration parameter */
    /** Constant <code>CLIENTS="clients"</code> */
    String CLIENTS = "clients";

    /* Authorizers names configuration parameter */
    /** Constant <code>AUTHORIZERS="authorizers"</code> */
    String AUTHORIZERS = "authorizers";

    /* The default url parameter */
    /** Constant <code>DEFAULT_URL="defaultUrl"</code> */
    String DEFAULT_URL = "defaultUrl";

    /* The client name servlet parameter */
    /** Constant <code>CLIENT_NAME="clientName"</code> */
    String CLIENT_NAME = "clientName";

    /* The default client */
    /** Constant <code>DEFAULT_CLIENT="defaultClient"</code> */
    String DEFAULT_CLIENT = "defaultClient";

    /* The default url, the root path */
    /** Constant <code>DEFAULT_URL_VALUE="/"</code> */
    String DEFAULT_URL_VALUE = "/";

    /* The url parameter */
    /** Constant <code>URL="url"</code> */
    String URL = "url";

    /* The element (client or authorizer) separator */
    /** Constant <code>ELEMENT_SEPARATOR=","</code> */
    String ELEMENT_SEPARATOR = ",";

    /** Constant <code>ADD_ELEMENT=""</code> */
    String ADD_ELEMENT = "+";

    /** Constant <code>TYPED_ID_SEPARATOR="#"</code> */
    String TYPED_ID_SEPARATOR = "#";

    /* The logout pattern for url */
    /** Constant <code>LOGOUT_URL_PATTERN="logoutUrlPattern"</code> */
    String LOGOUT_URL_PATTERN = "logoutUrlPattern";

    /* The default value for the logout url pattern, meaning only relative urls are allowed */
    /** Constant <code>DEFAULT_LOGOUT_URL_PATTERN_VALUE="^(\\/|\\/[^\\/].*)$"</code> */
    String DEFAULT_LOGOUT_URL_PATTERN_VALUE = "^(\\/|\\/[^\\/].*)$";

    /* The config factory parameter */
    /** Constant <code>CONFIG_FACTORY="configFactory"</code> */
    String CONFIG_FACTORY = "configFactory";

    /* Matcher names configuration parameter */
    /** Constant <code>MATCHERS="matchers"</code> */
    String MATCHERS = "matchers";

    /** Constant <code>USERNAME="username"</code> */
    String USERNAME = "username";

    /** Constant <code>PASSWORD="password"</code> */
    String PASSWORD = "password";

    /** Whether the session must be renewed after login. */
    String RENEW_SESSION = "renewSession";

    /** Whether a local logout must be performed */
    String LOCAL_LOGOUT = "localLogout";

    /** Whether we must destroy the web session during the local logout */
    String DESTROY_SESSION = "destroySession";

    /** Whether a central logout must be performed */
    String CENTRAL_LOGOUT = "centralLogout";

    /** Constant <code>DEFAULT_REALM_NAME="authentication required"</code> */
    String DEFAULT_REALM_NAME = "authentication required";

    /** Constant <code>OIDC_CLAIM_SESSIONID="sid"</code> */
    String OIDC_CLAIM_SESSIONID = "sid";

    /* An AJAX parameter name to dynamically set a HTTP request as an AJAX one. */
    /** Constant <code>IS_AJAX_REQUEST="is_ajax_request"</code> */
    String IS_AJAX_REQUEST = "is_ajax_request";

    /** The default client name parameter used on callback */
    String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    /** The default client name parameter used for security */
    String DEFAULT_FORCE_CLIENT_PARAMETER = "force_client";

    /** Constant <code>EMPTY_STRING=""</code> */
    String EMPTY_STRING = "";
}
