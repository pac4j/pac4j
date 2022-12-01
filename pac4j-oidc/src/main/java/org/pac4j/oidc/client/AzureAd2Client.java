package org.pac4j.oidc.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.PathParameterCallbackUrlResolver;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.oidc.client.azuread.AzureAdResourceRetriever;
import org.pac4j.oidc.config.AzureAd2OidcConfiguration;
import org.pac4j.oidc.profile.azuread.AzureAdProfile;
import org.pac4j.oidc.profile.azuread.AzureAdProfileCreator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class is the OpenID Connect client to authenticate users in Microsoft Azure AD v2.</p>.
 * <p>More information at: https://docs.microsoft.com/azure/active-directory/develop/active-directory-v2-protocols</p>
 *
 *  @author Charley Wu
 *  @since 5.0.0
 */
public class AzureAd2Client extends OidcClient {

    protected ObjectMapper objectMapper;
    protected static final TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};

    public AzureAd2Client() {
        objectMapper = new ObjectMapper();
    }

    public AzureAd2Client(AzureAd2OidcConfiguration configuration) {
        super(configuration);
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        getConfiguration().setResourceRetriever(new AzureAdResourceRetriever());
        defaultProfileCreator(new AzureAdProfileCreator(getConfiguration(), this));

        super.internalInit(forceReinit);
    }

    @Override
    protected CallbackUrlResolver newDefaultCallbackUrlResolver() {
        return new PathParameterCallbackUrlResolver();
    }

    /**
     * <p>Refresh the access token</p>
     * <p>https://docs.microsoft.com/azure/active-directory/develop/v2-oauth2-auth-code-flow#refresh-the-access-token</p>
     */
    public String getAccessTokenFromRefreshToken(final AzureAdProfile azureAdProfile) {
        val azureConfig = (AzureAd2OidcConfiguration) getConfiguration();
        HttpURLConnection connection = null;
        try {
            final Map<String, String> headers = new HashMap<>();
            headers.put(HttpConstants.CONTENT_TYPE_HEADER, HttpConstants.APPLICATION_FORM_ENCODED_HEADER_VALUE);
            headers.put(HttpConstants.ACCEPT_HEADER, HttpConstants.APPLICATION_JSON);
            // get the token endpoint from discovery URI
            val tokenEndpointURL = azureConfig.findProviderMetadata().getTokenEndpointURI().toURL();
            connection = HttpUtils.openPostConnection(tokenEndpointURL, headers);

            val out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),
                StandardCharsets.UTF_8));
            out.write(azureConfig.makeOauth2TokenRequest(azureAdProfile.getRefreshToken().getValue()));
            out.close();

            val responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new TechnicalException("request for access token failed: " + HttpUtils.buildHttpErrorMessage(connection));
            }
            var body = HttpUtils.readBody(connection);
            final Map<String, Object> res = objectMapper.readValue(body, typeRef);
            return (String) res.get("access_token");
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }
}
