package org.pac4j.core.logout.processor;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.HttpAction;

/**
 * Logout processor.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@FunctionalInterface
public interface LogoutProcessor {

    HttpAction processLogout(CallContext ctx, Credentials credentials);
}
