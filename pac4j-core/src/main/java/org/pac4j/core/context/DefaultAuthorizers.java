package org.pac4j.core.context;

/**
 * Constants for authorizers.
 *
 * @author Richard Walker
 * @since 2.0.0
 */
public interface DefaultAuthorizers {

    /** The "allowAjaxRequests" authorizer. */
    String ALLOW_AJAX_REQUESTS = "allowAjaxRequests";

    /** The "csrf" authorizer. */
    String CSRF = "csrf";

    /** The "csrfCheck" authorizer. */
    String CSRF_CHECK = "csrfCheck";

    /** The "csrfToken" authorizer. */
    String CSRF_TOKEN = "csrfToken";

    /** The "hsts" authorizer. */
    String HSTS = "hsts";

    /** The "isAnonymous" authorizer. */
    String IS_ANONYMOUS = "isAnonymous";

    /** The "isAuthenticated" authorizer. */
    String IS_AUTHENTICATED = "isAuthenticated";

    /** The "isFullyAuthenticated" authorizer. */
    String IS_FULLY_AUTHENTICATED = "isFullyAuthenticated";

    /** The "isRemembered" authorizer. */
    String IS_REMEMBERED = "isRemembered";

    /** The "nocache" authorizer. */
    String NOCACHE = "nocache";

    /** The "noframe" authorizer. */
    String NOFRAME = "noframe";

    /** The "nosniff" authorizer. */
    String NOSNIFF = "nosniff";

    /** The "securityheaders" authorizer. */
    String SECURITYHEADERS = "securityheaders";

    /** The "xssprotection" authorizer. */
    String XSSPROTECTION = "xssprotection";

}
