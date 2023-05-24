package org.pac4j.core.credentials.extractor;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;

import java.util.Optional;

/**
 * To extract a parameter value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@ToString
public class ParameterExtractor implements CredentialsExtractor {

    private final String parameterName;

    private boolean supportGetRequest;

    private boolean supportPostRequest;

    /**
     * <p>Constructor for ParameterExtractor.</p>
     *
     * @param parameterName a {@link String} object
     */
    public ParameterExtractor(final String parameterName) {
        this(parameterName, false, true);
    }

    /**
     * <p>Constructor for ParameterExtractor.</p>
     *
     * @param parameterName a {@link String} object
     * @param supportGetRequest a boolean
     * @param supportPostRequest a boolean
     */
    public ParameterExtractor(final String parameterName, final boolean supportGetRequest,
                              final boolean supportPostRequest) {
        this.parameterName = parameterName;
        this.supportGetRequest = supportGetRequest;
        this.supportPostRequest = supportPostRequest;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();
        if (WebContextHelper.isGet(webContext) && !supportGetRequest) {
            throw new CredentialsException("GET requests not supported");
        } else if (WebContextHelper.isPost(webContext) && !supportPostRequest) {
            throw new CredentialsException("POST requests not supported");
        }

        val value = webContext.getRequestParameter(this.parameterName);
        if (value.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new TokenCredentials(value.get()));
    }
}
