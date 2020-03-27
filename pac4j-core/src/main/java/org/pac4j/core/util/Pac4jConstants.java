package org.pac4j.core.util;

/**
 * Common constants.
 *
 * @author Jerome Leleu
 * @since 1.6.0
 */
public interface Pac4jConstants {

    /* Original requested url to save before redirect to Identity Provider */
    String REQUESTED_URL = "pac4jRequestedUrl";

    /* User profiles object saved in session */
    String USER_PROFILES = "pac4jUserProfiles";

    /* CSRF token name saved in session */
    String CSRF_TOKEN = "pac4jCsrfToken";

    /* Session ID */
    String SESSION_ID = "pac4jSessionId";

    /* Client names configuration parameter */
    String CLIENTS = "clients";

    /* An AJAX parameter name to dynamically set a HTTP request as an AJAX one. */
    String IS_AJAX_REQUEST = "is_ajax_request";

    /* Authorizers names configuration parameter */
    String AUTHORIZERS = "authorizers";

    /* The default url parameter */
    String DEFAULT_URL = "defaultUrl";

    /* The default client */
    String DEFAULT_CLIENT = "defaultClient";

    /* The default url, the root path */
    String DEFAULT_URL_VALUE = "/";

    /* The url parameter */
    String URL = "url";

    /* The element (client or authorizer) separator */
    String ELEMENT_SEPARATOR = ",";

    /* The logout pattern for url */
    String LOGOUT_URL_PATTERN = "logoutUrlPattern";

    /* The default value for the logout url pattern, meaning only relative urls are allowed */
    String DEFAULT_LOGOUT_URL_PATTERN_VALUE = "/.*";

    /* The config factory parameter */
    String CONFIG_FACTORY = "configFactory";

    /* Matcher names configuration parameter */
    String MATCHERS = "matchers";

    String USERNAME = "username";

    String PASSWORD = "password";

    /* Whether the profile should be saved into the session */
    String SAVE_IN_SESSION = "saveInSession";

    /* Whether multiple profiles are accepted */
    String MULTI_PROFILE = "multiProfile";

    /** Whether the session must be renewed after login. */
    String RENEW_SESSION = "renewSession";

    /** Whether a local logout must be performed */
    String LOCAL_LOGOUT = "localLogout";

    /** Whether we must destroy the web session during the local logout */
    String DESTROY_SESSION = "destroySession";

    /** Whether a central logout must be performed */
    String CENTRAL_LOGOUT = "centralLogout";

    String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    String DEFAULT_REALM_NAME = "authentication required";

    String LOGOUT_ENDPOINT_PARAMETER = "logoutendpoint";

    String OIDC_CLAIM_SESSIONID = "sid";

    String LOAD_PROFILES_FROM_SESSION = "pac4jLoadProfilesFromSession";
}
