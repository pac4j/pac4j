package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.HeaderExtractor;

/**
 * <p>This class is the client to authenticate users directly based on a provided header.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderClient extends DirectClientV2<TokenCredentials, CommonProfile> {

    private String headerName = "";

    private String prefixHeader = "";

    public HeaderClient() {}

    @Deprecated
    public HeaderClient(final Authenticator tokenAuthenticator) {
        setAuthenticator(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        setAuthenticator(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        setAuthenticator(tokenAuthenticator);
    }

    @Deprecated
    public HeaderClient(final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator,
                        final ProfileCreator profileCreator) {
        this.headerName = headerName;
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        setAuthenticator(tokenAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

        setCredentialsExtractor(new HeaderExtractor(this.headerName, this.prefixHeader, getName()));
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getPrefixHeader() {
        return prefixHeader;
    }

    public void setPrefixHeader(String prefixHeader) {
        this.prefixHeader = prefixHeader;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "headerName", this.headerName,
                "prefixHeader", this.prefixHeader, "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
