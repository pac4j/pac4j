package org.pac4j.core.credentials.extractor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.credentials.CredentialSource;
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
@RequiredArgsConstructor
@Slf4j
public class FormExtractor implements CredentialsExtractor {

    private final String usernameParameter;

    private final String passwordParameter;

    @Setter
    private ExtractionMode extractionMode = ExtractionMode.ALL;

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
                    if (WebContextHelper.isQueryStringParameter(webContext, this.usernameParameter)) {
                        username = webContext.getRequestParameter(this.usernameParameter);
                    }
                    if (WebContextHelper.isQueryStringParameter(webContext, this.passwordParameter)) {
                        password = webContext.getRequestParameter(this.passwordParameter);
                    }
                } catch (final Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
                break;
            case REQUEST_BODY:
                if ("POST".equalsIgnoreCase(webContext.getRequestMethod())) {
                    if (!WebContextHelper.isQueryStringParameter(webContext, this.usernameParameter)) {
                        username = webContext.getRequestParameter(this.usernameParameter);
                    }
                    if (!WebContextHelper.isQueryStringParameter(webContext, this.passwordParameter)) {
                        password = webContext.getRequestParameter(this.passwordParameter);
                    }
                }
                break;
        }

        if (username.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }
        val upc = new UsernamePasswordCredentials(username.get(), password.get());
        upc.setSource(CredentialSource.FORM.name());
        return Optional.of(upc);
    }

    public enum ExtractionMode {
        QUERY_PARAM,
        REQUEST_BODY,
        ALL
    }
}
