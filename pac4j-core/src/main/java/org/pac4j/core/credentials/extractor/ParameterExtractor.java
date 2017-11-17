package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * To extract a parameter value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ParameterExtractor implements CredentialsExtractor<TokenCredentials> {

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
    public Optional<TokenCredentials> extract(WebContext context) {
        final String method = context.getRequestMethod();
        if (HTTP_METHOD.GET.name().equalsIgnoreCase(method) && !supportGetRequest) {
            throw new CredentialsException("GET requests not supported");
        } else if (HTTP_METHOD.POST.name().equalsIgnoreCase(method) && !supportPostRequest) {
            throw new CredentialsException("POST requests not supported");
        }

        return Optional.ofNullable(context.getRequestParameter(this.parameterName))
            .map((v) -> new TokenCredentials(v));
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "parameterName", parameterName,
                "supportGetRequest", supportGetRequest, "supportPostRequest", supportPostRequest);
    }
}
