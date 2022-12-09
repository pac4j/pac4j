package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

/**
 * <p>This class is the client to authenticate users directly based on a provided parameter (in a GET and/or POST request).</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString
public class ParameterClient extends DirectClient {

    private String parameterName = Pac4jConstants.EMPTY_STRING;

    private boolean supportGetRequest = false;

    private boolean supportPostRequest = true;

    public ParameterClient() {}

    public ParameterClient(final String parameterName, final Authenticator tokenAuthenticator) {
        this.parameterName = parameterName;
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    public ParameterClient(final String parameterName,
                           final ProfileCreator profileCreator) {
        this.parameterName = parameterName;
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    public ParameterClient(final String parameterName,
                           final Authenticator tokenAuthenticator,
                           final ProfileCreator profileCreator) {
        this.parameterName = parameterName;
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }


    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("parameterName", this.parameterName);

        setCredentialsExtractorIfUndefined(new ParameterExtractor(this.parameterName, this.supportGetRequest, this.supportPostRequest));
    }
}
