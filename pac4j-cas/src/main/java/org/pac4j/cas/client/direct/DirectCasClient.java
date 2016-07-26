package org.pac4j.cas.client.direct;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the client to authenticate users on a CAS server for a web application in a stateless way: when trying to access a protected area,
 * the user will be redirected to the CAS server for login and then back directly to this originally requested url.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DirectCasClient extends DirectClientV2<CasCredentials, CasProfile> {

    private CasConfiguration configuration;

    public DirectCasClient() { }

    public DirectCasClient(final CasConfiguration casConfiguration) {
        this.configuration = casConfiguration;
    }

    @Override
    public CasCredentials getCredentials(final WebContext context) throws HttpAction {
        init(context);
        try {
            final CasCredentials credentials = getCredentialsExtractor().extract(context);
            // no credentials, we should redirect to the CAS server
            if (credentials == null) {

                return null;
            }
            getAuthenticator().validate(credentials, context);
            return credentials;
        } catch (CredentialsException e) {
            logger.error("Failed to retrieve or validate credentials", e);
            return null;
        }
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);

        setCredentialsExtractor(new ParameterExtractor(CasConfiguration.SERVICE_TICKET_PARAMETER, true, false, getName()));
        setAuthenticator(null);

        super.internalInit(context);
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(CasConfiguration configuration) {
        this.configuration = configuration;
    }
}
