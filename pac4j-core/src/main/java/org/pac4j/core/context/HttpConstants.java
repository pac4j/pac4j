package org.pac4j.core.context;

/**
 * Some HTTP constants.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface HttpConstants {

    int OK = 200;

    int CREATED = 201;

    int NO_CONTENT = 204;

    int UNAUTHORIZED = 401;

    int FORBIDDEN = 403;

    int FOUND = 302;

    int SEE_OTHER = 303;

    int TEMPORARY_REDIRECT = 307;

    int BAD_REQUEST = 400;

    int DEFAULT_HTTP_PORT = 80;

    int DEFAULT_HTTPS_PORT = 443;

    String SCHEME_HTTP = "http";

    String SCHEME_HTTPS = "https";

    int DEFAULT_CONNECT_TIMEOUT = 500;

    int DEFAULT_READ_TIMEOUT = 5000;

    String LOCATION_HEADER = "Location";

    String AUTHORIZATION_HEADER = "Authorization";

    String BASIC_HEADER_PREFIX = "Basic ";

    String BEARER_HEADER_PREFIX = "Bearer ";

    String DIGEST_HEADER_PREFIX = "Digest ";

    String ACCEPT_HEADER = "Accept";

    String APPLICATION_FORM_ENCODED_HEADER_VALUE = "application/x-www-form-urlencoded";

    String APPLICATION_JSON = "application/json";

    String AUTHENTICATE_HEADER = "WWW-Authenticate";

    String CONTENT_TYPE_HEADER = "Content-Type";

    String HTML_CONTENT_TYPE = "text/html; charset=utf-8";

    String AJAX_HEADER_VALUE = "XMLHttpRequest";

    String AJAX_HEADER_NAME = "X-Requested-With";

    String FACES_PARTIAL_AJAX_PARAMETER = "javax.faces.partial.ajax";

    enum HTTP_METHOD { GET, POST, HEAD, TRACE, PUT, DELETE, OPTIONS, PATCH }

    String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";

    String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";

    String ACCESS_CONTROL_ALLOW_CREDENTIALS_HEADER = "Access-Control-Allow-Credentials";

    String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";

    String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
}
