package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
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
    public final HttpAction redirect(final WebContext context) throws HttpAction {
        throw new TechnicalException("direct clients do not support redirections");
    }

    @Override
    public final C getCredentials(final WebContext context) throws HttpAction {
        init(context);

        return retrieveCredentials(context);
    }
}
