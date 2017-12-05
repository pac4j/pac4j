package org.pac4j.oidc.profile.azuread;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.oidc.config.AzureAdOidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class is the user profile for Azure AD (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.AzureAdClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class AzureAdProfile extends OidcProfile {

    private static final long serialVersionUID = -8659029290353954198L;

    private AzureAdOidcConfiguration config = null;

    public void setConfiguration(AzureAdOidcConfiguration config) {
        this.config = config;
    }

    public String getIdp() {
        return (String) getAttribute(AzureAdProfileDefinition.IDP);
    }

    public String getOid() {
        return (String) getAttribute(AzureAdProfileDefinition.OID);
    }

    public String getTid() {
        return (String) getAttribute(AzureAdProfileDefinition.TID);
    }

    public String getVer() {
        return (String) getAttribute(AzureAdProfileDefinition.VER);
    }

    public String getUniqueName() {
        return (String) getAttribute(AzureAdProfileDefinition.UNQIUE_NAME);
    }

    public String getIpaddr() {
        return (String) getAttribute(AzureAdProfileDefinition.IPADDR);
    }

    public String getUpn() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(AzureAdProfileDefinition.UPN);
    }

    public String getAccessTokenFromRefreshToken() {
        CommonHelper.assertTrue(CommonHelper.isNotBlank(config.getTenant()),
            "Tenant must be defined. Update your config.");
        HttpURLConnection connection = null;
        try {
            final Map<String, String> headers = new HashMap<>();
            headers.put( HttpConstants.CONTENT_TYPE_HEADER, HttpConstants.APPLICATION_FORM_ENCODED_HEADER_VALUE);
            headers.put( HttpConstants.ACCEPT_HEADER, HttpConstants.APPLICATION_JSON);

            connection = HttpUtils.openPostConnection(new URL("https://login.microsoftonline.com/"+config.getTenant()+"/oauth2/token"),
                headers);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),
                StandardCharsets.UTF_8));
            out.write(config.makeOauth2TokenRequest(getRefreshToken().getValue()));
            out.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new TechnicalException("request for access token failed: " + HttpUtils.buildHttpErrorMessage(connection));
            }
            return HttpUtils.readBody(connection);
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

}
