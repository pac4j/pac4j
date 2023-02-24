package org.pac4j.core.context;

/**
 * Some HTTP constants.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface HttpConstants {

    /** Constant <code>OK=200</code> */
    int OK = 200;

    /** Constant <code>CREATED=201</code> */
    int CREATED = 201;

    /** Constant <code>NO_CONTENT=204</code> */
    int NO_CONTENT = 204;

    /** Constant <code>UNAUTHORIZED=401</code> */
    int UNAUTHORIZED = 401;

    /** Constant <code>FORBIDDEN=403</code> */
    int FORBIDDEN = 403;

    /** Constant <code>FOUND=302</code> */
    int FOUND = 302;

    /** Constant <code>SEE_OTHER=303</code> */
    int SEE_OTHER = 303;

    /** Constant <code>TEMPORARY_REDIRECT=307</code> */
    int TEMPORARY_REDIRECT = 307;

    /** Constant <code>BAD_REQUEST=400</code> */
    int BAD_REQUEST = 400;

    /** Constant <code>DEFAULT_HTTP_PORT=80</code> */
    int DEFAULT_HTTP_PORT = 80;

    /** Constant <code>DEFAULT_HTTPS_PORT=443</code> */
    int DEFAULT_HTTPS_PORT = 443;

    /** Constant <code>SCHEME_HTTP="http"</code> */
    String SCHEME_HTTP = "http";

    /** Constant <code>SCHEME_HTTPS="https"</code> */
    String SCHEME_HTTPS = "https";

    /** Constant <code>DEFAULT_CONNECT_TIMEOUT=500</code> */
    int DEFAULT_CONNECT_TIMEOUT = 500;

    /** Constant <code>DEFAULT_READ_TIMEOUT=5000</code> */
    int DEFAULT_READ_TIMEOUT = 5000;

    /** Constant <code>LOCATION_HEADER="Location"</code> */
    String LOCATION_HEADER = "Location";

    /** Constant <code>AUTHORIZATION_HEADER="Authorization"</code> */
    String AUTHORIZATION_HEADER = "Authorization";

    /** Constant <code>BASIC_HEADER_PREFIX="Basic "</code> */
    String BASIC_HEADER_PREFIX = "Basic ";

    /** Constant <code>BEARER_HEADER_PREFIX="Bearer "</code> */
    String BEARER_HEADER_PREFIX = "Bearer ";

    /** Constant <code>DIGEST_HEADER_PREFIX="Digest "</code> */
    String DIGEST_HEADER_PREFIX = "Digest ";

    /** Constant <code>ACCEPT_HEADER="Accept"</code> */
    String ACCEPT_HEADER = "Accept";

    /** Constant <code>APPLICATION_FORM_ENCODED_HEADER_VALUE="application/x-www-form-urlencoded"</code> */
    String APPLICATION_FORM_ENCODED_HEADER_VALUE = "application/x-www-form-urlencoded";

    /** Constant <code>APPLICATION_JSON="application/json"</code> */
    String APPLICATION_JSON = "application/json";

    /** Constant <code>AUTHENTICATE_HEADER="WWW-Authenticate"</code> */
    String AUTHENTICATE_HEADER = "WWW-Authenticate";

    /** Constant <code>CONTENT_TYPE_HEADER="Content-Type"</code> */
    String CONTENT_TYPE_HEADER = "Content-Type";

    /** Constant <code>HTML_CONTENT_TYPE="text/html; charset=utf-8"</code> */
    String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    /** Constant <code>AJAX_HEADER_VALUE="XMLHttpRequest"</code> */
    String AJAX_HEADER_VALUE = "XMLHttpRequest";

    /** Constant <code>AJAX_HEADER_NAME="X-Requested-With"</code> */
    String AJAX_HEADER_NAME = "X-Requested-With";

    /** Constant <code>FACES_PARTIAL_AJAX_PARAMETER="javax.faces.partial.ajax"</code> */
    String FACES_PARTIAL_AJAX_PARAMETER = "javax.faces.partial.ajax";

    enum HTTP_METHOD { GET, POST, HEAD, TRACE, PUT, DELETE, OPTIONS, PATCH }

    /** Constant <code>ACCESS_CONTROL_ALLOW_ORIGIN_HEADER="Access-Control-Allow-Origin"</code> */
    String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";

    /** Constant <code>ACCESS_CONTROL_EXPOSE_HEADERS_HEADER="Access-Control-Expose-Headers"</code> */
    String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    /** Constant <code>ACCESS_CONTROL_MAX_AGE_HEADER="Access-Control-Max-Age"</code> */
    String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";

    /** Constant <code>ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER="Access-Control-Allow-Credentials"</code> */
    String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";

    /** Constant <code>ACCESS_CONTROL_ALLOW_METHODS_HEADER="Access-Control-Allow-Methods"</code> */
    String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";

    /** Constant <code>ACCESS_CONTROL_ALLOW_HEADERS_HEADER="Access-Control-Allow-Headers"</code> */
    String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
}
