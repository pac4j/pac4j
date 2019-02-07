package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.Optional;

/**
 * To extract a username and password posted from a form.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class FormExtractor implements CredentialsExtractor<UsernamePasswordCredentials> {

    private final String usernameParameter;

    private final String passwordParameter;

    public FormExtractor(final String usernameParameter, final String passwordParameter) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    @Override
    public Optional<UsernamePasswordCredentials> extract(WebContext context) {
        final Optional<String> username = context.getRequestParameter(this.usernameParameter);
        final Optional<String> password = context.getRequestParameter(this.passwordParameter);
        if (!username.isPresent() || !password.isPresent()) {
            return Optional.empty();
        }

        return Optional.of(new UsernamePasswordCredentials(username.get(), password.get()));
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }
}
