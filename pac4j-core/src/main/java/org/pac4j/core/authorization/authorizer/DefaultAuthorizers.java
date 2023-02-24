package org.pac4j.core.authorization.authorizer;

/**
 * Constants for authorizers.
 *
 * @author Richard Walker
 * @since 2.0.0
 */
public interface DefaultAuthorizers {

    /** The "csrfCheck" authorizer. */
    String CSRF_CHECK = "csrfCheck";

    /** The "isAnonymous" authorizer. */
    String IS_ANONYMOUS = "isAnonymous";

    /** The "isAuthenticated" authorizer. */
    String IS_AUTHENTICATED = "isAuthenticated";

    /** The "isFullyAuthenticated" authorizer. */
    String IS_FULLY_AUTHENTICATED = "isFullyAuthenticated";

    /** The "isRemembered" authorizer. */
    String IS_REMEMBERED = "isRemembered";

    /** Constant <code>NONE="none"</code> */
    String NONE = "none";
}
