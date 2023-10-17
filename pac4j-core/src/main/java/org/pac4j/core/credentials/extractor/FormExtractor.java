package org.pac4j.core.credentials.extractor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.core5.net.URIBuilder;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.Optional;

/**
 * To extract a username and password posted from a form.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Slf4j
public class FormExtractor implements CredentialsExtractor {

    private final String usernameParameter;

    private final String passwordParameter;

    @Setter
    private ExtractionMode extractionMode = ExtractionMode.ALL;

    /**
     * <p>Constructor for FormExtractor.</p>
     *
     * @param usernameParameter a {@link String} object
     * @param passwordParameter a {@link String} object
     */
    public FormExtractor(final String usernameParameter, final String passwordParameter) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        val webContext = ctx.webContext();

        var username = Optional.<String>empty();
        var password = Optional.<String>empty();
        switch (extractionMode) {
            case ALL:
                username = webContext.getRequestParameter(this.usernameParameter);
                password = webContext.getRequestParameter(this.passwordParameter);
                break;
            case QUERY_PARAM:
                try {
                    val uriBuilder = new URIBuilder(webContext.getFullRequestURL());
                    username = Optional.ofNullable(uriBuilder.getFirstQueryParam(this.usernameParameter).getValue());
                    password = Optional.ofNullable(uriBuilder.getFirstQueryParam(this.passwordParameter).getValue());
                } catch (final Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
                break;
            case REQUEST_BODY:
                if ("POST".equalsIgnoreCase(webContext.getRequestMethod())) {
                    username = webContext.getRequestParameter(this.usernameParameter);
                    password = webContext.getRequestParameter(this.passwordParameter);
                }
                break;
        }

        if (username.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new UsernamePasswordCredentials(username.get(), password.get()));
    }

    public enum ExtractionMode {
        QUERY_PARAM,
        REQUEST_BODY,
        ALL
    }
}
