package org.pac4j.core.credentials.extractor;

import lombok.Getter;
import lombok.val;
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
public class FormExtractor implements CredentialsExtractor {

    private final String usernameParameter;

    private final String passwordParameter;

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
        val username = webContext.getRequestParameter(this.usernameParameter);
        val password = webContext.getRequestParameter(this.passwordParameter);
        if (username.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new UsernamePasswordCredentials(username.get(), password.get()));
    }
}
