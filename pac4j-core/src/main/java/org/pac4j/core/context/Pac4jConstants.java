package org.pac4j.core.context;

public interface Pac4jConstants {

    /* Original requested url to save before redirect to Identity Provider */
    public final static String REQUESTED_URL = "pac4jRequestedUrl";

    /* User Profile object saved in session */
    public final static String USER_PROFILE = "pac4jUserProfile";

    /* Session ID */
    public final static String SESSION_ID = "pac4jSessionId";

    /* Client name configuration parameter */
    public final static String CLIENT_NAME = "clientName";

    /* Stateless configuration parameter */
    public final static String STATELESS = "stateless";

    /* Is Ajax configuration parameter */
    public final static String IS_AJAX = "isAjax";

    /* RequireAnyRole configuration parameter */
    public final static String REQUIRE_ANY_ROLE = "requireAnyRole";

    /* RequireAllRoles configuration parameter */
    public final static String REQUIRE_ALL_ROLES = "requireAllRoles";

    /* Target url configuration parameter */
    public final static String TARGET_URL = "targetUrl";

}
