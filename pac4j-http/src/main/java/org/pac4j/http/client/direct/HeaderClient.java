package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
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
public class HeaderClient extends DirectClient<TokenCredentials> {

    private String headerName = "";

    private String prefixHeader = "";

    public HeaderClient() {}

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        defaultAuthenticator(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        defaultAuthenticator(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator,
                        final ProfileCreator profileCreator) {
        this.headerName = headerName;
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        defaultAuthenticator(tokenAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit() {
        if (getCredentialsExtractor() == null) {
            CommonHelper.assertNotBlank("headerName", this.headerName);
            CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

            defaultCredentialsExtractor(new HeaderExtractor(this.headerName, this.prefixHeader));
        }
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
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "headerName", this.headerName,
                "prefixHeader", this.prefixHeader, "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
                "profileCreator", getProfileCreator());
    }
}
