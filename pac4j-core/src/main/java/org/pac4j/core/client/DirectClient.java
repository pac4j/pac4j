package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;

/**
 * <p>This class is the default direct (stateless) implementation of an authentication client (whatever the mechanism).
 * In that case, redirecting does not make any sense.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class DirectClient<C extends Credentials, U extends CommonProfile> extends BaseClient<C, U> {

    @Override
    public final void redirect(final WebContext context) throws RequiresHttpAction {
        throw new TechnicalException("direct clients do not support redirections");
    }
}
