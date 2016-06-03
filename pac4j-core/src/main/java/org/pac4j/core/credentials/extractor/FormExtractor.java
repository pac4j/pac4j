package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.HttpAction;

/**
 * To extract a username and password posted from a form.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class FormExtractor implements CredentialsExtractor<UsernamePasswordCredentials> {

    private final String usernameParameter;

    private final String passwordParameter;

    private final String clientName;

    public FormExtractor(final String usernameParameter, final String passwordParameter, final String clientName) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        this.clientName = clientName;
    }

    @Override
    public UsernamePasswordCredentials extract(WebContext context) throws HttpAction {
        final String username = context.getRequestParameter(this.usernameParameter);
        final String password = context.getRequestParameter(this.passwordParameter);
        if (username == null || password == null) {
            return null;
        }

        return new UsernamePasswordCredentials(username, password, clientName);
    }

    public String getUsernameParameter() {
        return usernameParameter;
    }

    public String getPasswordParameter() {
        return passwordParameter;
    }
}
