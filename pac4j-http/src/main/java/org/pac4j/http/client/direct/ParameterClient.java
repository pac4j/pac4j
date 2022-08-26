package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.toNiceString;

/**
 * <p>This class is the client to authenticate users directly based on a provided parameter (in a GET and/or POST request).</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ParameterClient extends DirectClient {

    private String parameterName = Pac4jConstants.EMPTY_STRING;

    private boolean supportGetRequest = false;

    private boolean supportPostRequest = true;

    public ParameterClient() {}

    public ParameterClient(final String parameterName, final Authenticator tokenAuthenticator) {
        this.parameterName = parameterName;
        defaultAuthenticator(tokenAuthenticator);
    }

    public ParameterClient(final String parameterName,
                           final Authenticator tokenAuthenticator,
                           final ProfileCreator profileCreator) {
        this.parameterName = parameterName;
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }


    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("parameterName", this.parameterName);

        defaultCredentialsExtractor(new ParameterExtractor(this.parameterName, this.supportGetRequest, this.supportPostRequest));
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public boolean isSupportGetRequest() {
        return supportGetRequest;
    }

    public void setSupportGetRequest(boolean supportGetRequest) {
        this.supportGetRequest = supportGetRequest;
    }

    public boolean isSupportPostRequest() {
        return supportPostRequest;
    }

    public void setSupportPostRequest(boolean supportPostRequest) {
        this.supportPostRequest = supportPostRequest;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "parameterName", this.parameterName,
                "supportGetRequest", this.supportGetRequest, "supportPostRequest", this.supportPostRequest,
                "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(), "profileCreator", getProfileCreator());
    }
}
