package org.pac4j.cas.credentials.extractor;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.Protocol;
import org.apereo.cas.client.util.XmlUtils;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.WebContextHelper;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.SessionKeyCredentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.util.CommonHelper;

import java.util.Base64;
import java.util.Optional;
import java.util.zip.Inflater;

/**
 * CAS credentials extractor.
 *
 * @author Jerome Leleu
 * @since 6.0.0
 */
@Slf4j
public class CasCredentialsExtractor implements CredentialsExtractor {

    private final static int DECOMPRESSION_FACTOR = 10;

    protected CasConfiguration configuration;

    public CasCredentialsExtractor(final CasConfiguration configuration) {
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
    }

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        Credentials credentials = null;

        val webContext = ctx.webContext();

        // like the SingleSignOutFilter from the Apereo CAS client:
        if (isTokenRequest(webContext)) {
            val ticket = getArtifactParameter(webContext).get();
            credentials = new TokenCredentials(ticket);

        } else if (isBackLogoutRequest(webContext)) {
            val logoutMessage = webContext.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).get();
            LOGGER.trace("Logout request:\n{}", logoutMessage);

            val ticket = XmlUtils.getTextForElement(logoutMessage, CasConfiguration.SESSION_INDEX_TAG);
            credentials = new SessionKeyCredentials(LogoutType.BACK, ticket);

        } else if (isFrontLogoutRequest(webContext)) {
            val logoutMessage = uncompressLogoutMessage(
                webContext.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).get());
            LOGGER.trace("Logout request:\n{}", logoutMessage);

            val ticket = XmlUtils.getTextForElement(logoutMessage, CasConfiguration.SESSION_INDEX_TAG);
            credentials = new SessionKeyCredentials(LogoutType.FRONT, ticket);
        }

        LOGGER.debug("extracted credentials: {}", credentials);
        return Optional.ofNullable(credentials);
    }

    protected boolean isTokenRequest(final WebContext context) {
        return getArtifactParameter(context).isPresent();
    }

    protected Optional<String> getArtifactParameter(final WebContext context) {
        if (configuration.getProtocol() == CasProtocol.SAML) {
            val optValue = context.getRequestParameter(Protocol.SAML11.getArtifactParameterName());
            if (optValue.isPresent()) {
                return optValue;
            }
        }
        return context.getRequestParameter(CasConfiguration.TICKET_PARAMETER);
    }

    protected boolean isBackLogoutRequest(final WebContext context) {
        return WebContextHelper.isPost(context)
                && !isMultipartRequest(context)
                && context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).isPresent();
    }

    protected boolean isMultipartRequest(final WebContext context) {
        val contentType = context.getRequestHeader(HttpConstants.CONTENT_TYPE_HEADER);
        return contentType.isPresent() && contentType.get().toLowerCase().startsWith("multipart");
    }

    protected boolean isFrontLogoutRequest(final WebContext context) {
        return WebContextHelper.isGet(context)
                && context.getRequestParameter(CasConfiguration.LOGOUT_REQUEST_PARAMETER).isPresent();
    }

    protected String uncompressLogoutMessage(final String originalMessage) {
        val binaryMessage = Base64.getMimeDecoder().decode(originalMessage);

        Inflater decompresser = null;
        try {
            // decompress the bytes
            decompresser = new Inflater();
            decompresser.setInput(binaryMessage);
            val result = new byte[binaryMessage.length * DECOMPRESSION_FACTOR];

            val resultLength = decompresser.inflate(result);

            // decode the bytes into a String
            return new String(result, 0, resultLength, "UTF-8");
        } catch (final Exception e) {
            LOGGER.error("Unable to decompress logout message", e);
            throw new TechnicalException(e);
        } finally {
            if (decompresser != null) {
                decompresser.end();
            }
        }
    }
}
