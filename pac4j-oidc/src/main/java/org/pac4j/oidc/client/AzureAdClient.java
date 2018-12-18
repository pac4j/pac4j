package org.pac4j.oidc.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.oidc.client.azuread.AzureAdResourceRetriever;
import org.pac4j.oidc.config.AzureAdOidcConfiguration;
import org.pac4j.oidc.profile.azuread.AzureAdProfile;
import org.pac4j.oidc.profile.azuread.AzureAdProfileCreator;

/**
 * A specialized {@link OidcClient} for authenticating againt Microsoft Azure AD. Microsoft Azure
 * AD provides authentication for multiple tenants, or, when the tenant is not known prior to
 * authentication, the speciall common-tenant. For a specific tenant, the following discovery URI
 * must be used:
 * {@code https://login.microsoftonline.com/tenantid/.well-known/openid-configuration} or
 * {@code https://login.microsoftonline.com/tenantid/v2.0/.well-known/openid-configuration} for
 * Azure AD v2.0. Replace {@code tenantid} with the ID of the tenant to authenticate against. To
 * find this ID, fill in your tenant's domain name. Your tenant ID is the UUID in
 * {@code authorization_endpoint}.
 *
 * For authentication against an unknown (or dynamic tenant), use {@code common} as ID.
 * Authentication against the common endpoint results in a ID token with a {@code issuer} different
 * from the {@code issuer} mentioned in the discovery data. This class uses to special validator
 * to correctly validate the issuer returned by Azure AD.
 *
 * More information at: https://msdn.microsoft.com/en-us/library/azure/dn645541.aspx
 *
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdClient extends OidcClient<AzureAdOidcConfiguration> {

    private ObjectMapper objectMapper;
    private static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

    public AzureAdClient() {}

    public AzureAdClient(final AzureAdOidcConfiguration configuration) {
        super(configuration);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("configuration", getConfiguration());
        getConfiguration().setResourceRetriever(new AzureAdResourceRetriever());
        defaultProfileCreator(new AzureAdProfileCreator(getConfiguration()));

        super.clientInit();
    }

    @Override
    protected CallbackUrlResolver newDefaultCallbackUrlResolver() {
        return new PathParameterCallbackUrlResolver();
    }

    public String getAccessTokenFromRefreshToken(AzureAdProfile azureAdProfile) {
        CommonHelper.assertTrue(CommonHelper.isNotBlank(getConfiguration().getTenant()),
            "Tenant must be defined. Update your config.");
        HttpURLConnection connection = null;
        try {
            final Map<String, String> headers = new HashMap<>();
            headers.put( HttpConstants.CONTENT_TYPE_HEADER, HttpConstants.APPLICATION_FORM_ENCODED_HEADER_VALUE);
            headers.put( HttpConstants.ACCEPT_HEADER, HttpConstants.APPLICATION_JSON);

            connection = HttpUtils.openPostConnection(new URL("https://login.microsoftonline.com/"+getConfiguration().getTenant()+
                "/oauth2/token"), headers);

            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),
                StandardCharsets.UTF_8));
            out.write(getConfiguration().makeOauth2TokenRequest(azureAdProfile.getRefreshToken().getValue()));
            out.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new TechnicalException("request for access token failed: " + HttpUtils.buildHttpErrorMessage(connection));
            }
            String body = HttpUtils.readBody(connection);
            final Map<String, Object> res = objectMapper.readValue(body, typeRef);
            return (String)res.get("access_token");
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }
}
