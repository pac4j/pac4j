package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

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
    public UsernamePasswordCredentials extract(WebContext context) {
        final String username = context.getRequestParameter(this.usernameParameter);
        final String password = context.getRequestParameter(this.passwordParameter);
        if (username == null || password == null) {
            return null;
        }

        return new UsernamePasswordCredentials(username, password);
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }
}
