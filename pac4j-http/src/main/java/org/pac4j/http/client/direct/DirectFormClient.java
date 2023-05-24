package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

/**
 * This class is the client to authenticate users, based on form HTTP parameters.
 *
 * @author Jerome Leleu
 * @since 1.8.6
 */
@Getter
@Setter
@ToString
public class DirectFormClient extends DirectClient {

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    /**
     * <p>Constructor for DirectFormClient.</p>
     */
    public DirectFormClient() {}

    /**
     * <p>Constructor for DirectFormClient.</p>
     *
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     */
    public DirectFormClient(final Authenticator usernamePasswordAuthenticator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    /**
     * <p>Constructor for DirectFormClient.</p>
     *
     * @param usernameParameter a {@link String} object
     * @param passwordParameter a {@link String} object
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     */
    public DirectFormClient(final String usernameParameter, final String passwordParameter,
                            final Authenticator usernamePasswordAuthenticator) {
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    /**
     * <p>Constructor for DirectFormClient.</p>
     *
     * @param usernamePasswordAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectFormClient(final Authenticator usernamePasswordAuthenticator,
                            final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("usernameParameter", usernameParameter);
        assertNotBlank("passwordParameter", passwordParameter);

        setCredentialsExtractorIfUndefined(new FormExtractor(usernameParameter, passwordParameter));
    }
}
