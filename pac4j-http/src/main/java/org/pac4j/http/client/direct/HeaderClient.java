package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.HeaderExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * <p>This class is the client to authenticate users directly based on a provided header.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString
public class HeaderClient extends DirectClient {

    private String headerName = Pac4jConstants.EMPTY_STRING;

    private String prefixHeader = Pac4jConstants.EMPTY_STRING;

    public HeaderClient() {}

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    public HeaderClient(final String headerName, final Authenticator tokenAuthenticator,
                        final ProfileCreator profileCreator) {
        this.headerName = headerName;
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    public HeaderClient(final String headerName, final ProfileCreator profileCreator) {
        this.headerName = headerName;
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    public HeaderClient(final String headerName, final String prefixHeader,
                        final Authenticator tokenAuthenticator, final ProfileCreator profileCreator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    public HeaderClient(final String headerName, final String prefixHeader, final ProfileCreator profileCreator) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        if (getCredentialsExtractor() == null) {
            CommonHelper.assertNotBlank("headerName", this.headerName);
            CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

            setCredentialsExtractorIfUndefined(new HeaderExtractor(this.headerName, this.prefixHeader));
        }
    }
}
