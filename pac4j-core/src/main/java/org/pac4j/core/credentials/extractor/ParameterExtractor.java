package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * To extract a parameter value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ParameterExtractor implements CredentialsExtractor {

    private final String parameterName;

    private boolean supportGetRequest;

    private boolean supportPostRequest;

    public ParameterExtractor(final String parameterName) {
        this(parameterName, false, true);
    }

    public ParameterExtractor(final String parameterName, final boolean supportGetRequest,
                              final boolean supportPostRequest) {
        this.parameterName = parameterName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
    }

    @Override
    public Optional<Credentials> extract(final WebContext context, final SessionStore sessionStore) {
        if (ContextHelper.isGet(context) && !supportGetRequest) {
            throw new CredentialsException("GET requests not supported");
        } else if (ContextHelper.isPost(context) && !supportPostRequest) {
            throw new CredentialsException("POST requests not supported");
        }

        final Optional<String> value = context.getRequestParameter(this.parameterName);
        if (!value.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new TokenCredentials(value.get()));
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "parameterName", parameterName,
                "supportGetRequest", supportGetRequest, "supportPostRequest", supportPostRequest);
    }
}
