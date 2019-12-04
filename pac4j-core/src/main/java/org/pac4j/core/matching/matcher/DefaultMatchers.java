package org.pac4j.core.matching.matcher;

/**
 * The default matchers.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface DefaultMatchers {

    /** The "hsts" authorizer. */
    String HSTS = "hsts";

    /** The "nosniff" authorizer. */
    String NOSNIFF = "nosniff";

    /** The "noframe" authorizer. */
    String NOFRAME = "noframe";

    /** The "securityheaders" authorizer. */
    String SECURITYHEADERS = "securityheaders";

    /** The "xssprotection" authorizer. */
    String XSSPROTECTION = "xssprotection";

    /** The "allowAjaxRequests" authorizer. */
    String ALLOW_AJAX_REQUESTS = "allowAjaxRequests";

    /** The "nocache" authorizer. */
    String NOCACHE = "nocache";

    /** The "csrfToken" authorizer. */
    String CSRF_TOKEN = "csrfToken";

    String GET = "get";
    String POST = "post";
    String PUT = "put";
    String DELETE = "delete";

    String NONE = "none";
}
