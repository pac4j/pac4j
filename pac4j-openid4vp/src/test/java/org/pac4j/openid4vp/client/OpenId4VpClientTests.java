package org.pac4j.openid4vp.client;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.http.ajax.DefaultAjaxRequestResolver;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.jwt.config.signature.ECSignatureConfiguration;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.util.TestsHelper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link OpenId4VpClient}, and along the way documents what a valid configuration is made of.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpClientTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";

    private static KeyPair buildEcKeyPair() throws Exception {
        val generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        return generator.generateKeyPair();
    }

    /**
     * The smallest configuration which passes the initialization. The client identifier prefix is set to
     * {@code redirect_uri} to keep a relying party certificate chain out of this test.
     */
    private static OpenId4VpConfiguration validConfiguration() throws Exception {
        val configuration = new OpenId4VpConfiguration();
        configuration.setClientId("verifier.example.org");
        configuration.setClientIdPrefix(ClientIdPrefix.REDIRECT_URI);
        configuration.setDcqlQuery("{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}");
        configuration.setRequestObjectSignatureConfiguration(new ECSignatureConfiguration(buildEcKeyPair()));
        configuration.addCredentialVerifier(new SdJwtVcVerifier());
        return configuration;
    }

    private static OpenId4VpClient validClient() throws Exception {
        val client = new OpenId4VpClient(validConfiguration());
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    void testTheDefaultResolverReturnsTheWalletUrl() throws Exception {
        val client = validClient();
        client.init();

        val resolver = assertInstanceOf(DefaultAjaxRequestResolver.class, client.getAjaxRequestResolver());
        assertTrue(resolver.isAddRedirectionUrlAsHeader(),
            "without it the redirection action builder is never called and no transaction is ever opened");
    }

    @Test
    void testTheDefaultResolverMustReturnTheWalletUrl() throws Exception {
        val client = validClient();
        client.setAjaxRequestResolver(new DefaultAjaxRequestResolver());

        TestsHelper.expectException(client::init, TechnicalException.class,
            "the addRedirectionUrlAsHeader property of the DefaultAjaxRequestResolver must be true: the wallet URL must be "
                + "returned to the application and the redirection action builder must run to open the transaction");
    }

    @Test
    void testACustomResolverIsAccepted() throws Exception {
        val client = validClient();
        // the check must not forbid another way of handing the wallet URL over, such as a JSON body
        client.setAjaxRequestResolver(new AjaxRequestResolver() {
            @Override
            public boolean isAjax(final CallContext ctx) {
                return false;
            }

            @Override
            public HttpAction buildAjaxResponse(final CallContext ctx, final RedirectionActionBuilder builder) {
                return null;
            }
        });

        client.init();
    }

    @Test
    void testTheX509PrefixNeedsACertificateChain() throws Exception {
        val client = validClient();
        client.getConfiguration().setClientIdPrefix(ClientIdPrefix.X509_SAN_DNS);

        TestsHelper.expectException(client::init, TechnicalException.class,
            "relyingPartyCertificateChain cannot be empty for the x509_san_dns client identifier prefix");
    }

    @Test
    void testEveryRequestedFormatNeedsAVerifier() throws Exception {
        val client = validClient();
        client.getConfiguration().getCredentialVerifiers().clear();

        TestsHelper.expectException(client::init, TechnicalException.class,
            "credentialVerifier for dc+sd-jwt cannot be null");
    }
}
