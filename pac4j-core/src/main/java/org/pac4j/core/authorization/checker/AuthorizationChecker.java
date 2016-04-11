package org.pac4j.core.authorization.checker;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Map;

/**
 * The way to check authorizations.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface AuthorizationChecker {

    boolean isAuthorized(WebContext context, List<CommonProfile> profiles, String authorizerNames, Map<String, Authorizer> authorizersMap) throws RequiresHttpAction;

    boolean isAuthorized(WebContext context, List<CommonProfile> profiles, List<Authorizer> authorizers) throws RequiresHttpAction;
}
