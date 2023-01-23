package org.pac4j.core.logout.processor;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.LogoutCredentials;
import org.pac4j.core.exception.http.HttpAction;

/**
 * Logout processor.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
public interface LogoutProcessor {

    HttpAction processLogout(CallContext ctx, LogoutCredentials credentials);
}
