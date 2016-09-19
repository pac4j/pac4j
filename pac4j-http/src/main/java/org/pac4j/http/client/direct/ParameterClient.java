package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;

/**
 * <p>This class is the client to authenticate users directly based on a provided parameter (in a GET and/or POST request).</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ParameterClient extends DirectClientV2<TokenCredentials, CommonProfile> {

    private String parameterName = "";

    private boolean supportGetRequest = false;

    private boolean supportPostRequest = true;

    public ParameterClient() {}

    public ParameterClient(final String parameterName, final Authenticator tokenAuthenticator) {
        this.parameterName = parameterName;
        setAuthenticator(tokenAuthenticator);
    }

    public ParameterClient(final String parameterName,
                           final Authenticator tokenAuthenticator,
                           final ProfileCreator profileCreator) {
        this.parameterName = parameterName;
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }


    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("parameterName", this.parameterName);

        setCredentialsExtractor(new ParameterExtractor(this.parameterName, this.supportGetRequest, this.supportPostRequest, getName()));
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
        return CommonHelper.toString(this.getClass(), "name", getName(), "parameterName", this.parameterName,
                "supportGetRequest", this.supportGetRequest, "supportPostRequest", this.supportPostRequest,
                "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(), "profileCreator", getProfileCreator());
    }
}
