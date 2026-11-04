package org.pac4j.saml.logout.impl;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SPSSODescriptorBuilder;
import org.opensaml.saml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.pac4j.core.context.CallContext;
import org.pac4j.test.context.session.MockSessionStore;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.ExcludingParametersURIComparator;
import org.pac4j.test.context.MockWebContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This is {@link SAML2LogoutValidatorTests}.
 *
 * @author Misagh Moayyed
 * @since 5.0.0
 */
public class SAML2LogoutValidatorTests {
    private static ExplicitSignatureTrustEngineProvider getTrustEngine() {
        var config = getSaml2Configuration();

        var idp = new SAML2IdentityProviderMetadataResolver(config);
        var sp = new SAML2ServiceProviderMetadataResolver(config);
        return new ExplicitSignatureTrustEngineProvider(idp, sp);
    }

    private static SAML2Configuration getSaml2Configuration() {
        val config = new SAML2Configuration();
        config.setForceKeystoreGeneration(true);
        config.getIdentityProviderMetadata().setResource(new ClassPathResource("idp-metadata.xml"));
        config.getServiceProviderMetadata().setResource(new FileSystemResource("target/out.xml"));
        config.setForceServiceProviderMetadataGeneration(true);
        config.setKeystorePath("target/keystore.jks");
        config.setKeystorePassword("pac4j");
        config.setPrivateKeyPassword("pac4j");
        config.init();
        return config;
    }

    private static MockWebContext getMockWebContext() {
        return MockWebContext.create();
    }

    private static SAML2MessageContext getSaml2MessageContext(final MockWebContext webContext, final String xml) {
        val context = new SAML2MessageContext(new CallContext(webContext, new MockSessionStore()));
        context.setSaml2Configuration(getSaml2Configuration());

        val samlMessage = new MessageContext();
        val samlResponse = (LogoutResponse) Configuration.deserializeSamlObject(xml).get();

        samlMessage.setMessage(samlResponse);
        context.setMessageContext(samlMessage);
        val entityDescriptor = new EntityDescriptorBuilder().buildObject();
        context.getSAMLPeerMetadataContext().setEntityDescriptor(entityDescriptor);

        val spDescriptor = new SPSSODescriptorBuilder().buildObject();
        val logoutService = new SingleLogoutServiceBuilder().buildObject();
        logoutService.setLocation("http://sp.example.com/demo1/logout");
        spDescriptor.getSingleLogoutServices().add(logoutService);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        return context;
    }

    @Test
    public void verifyHostComparison() {
        val xml = "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" " +
            "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" " +
            "ID=\"_6c3737282f007720e736f0f4028feed8cb9b40291c\" Version=\"2.0\" " +
            "IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC)
            + "\" Destination=\"http://sp.example.com/demo1/logout?x=1000%26y=1234\" " +
            "InResponseTo=\"ONELOGIN_21df91a89767879fc0f7df6a1490c6000c81644d\">" +
            "  <saml:Issuer>http://idp.example.com/metadata.php</saml:Issuer>" +
            "  <samlp:Status>" +
            "    <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Success\"/>" +
            "  </samlp:Status>" +
            "</samlp:LogoutResponse>";

        try {
            val webContext = getMockWebContext();
            val context = getSaml2MessageContext(webContext, xml);
            SAML2ResponseValidator validator = new SAML2LogoutValidator(
                getTrustEngine(),
                mock(Decrypter.class),
                mock(SessionLogoutHandler.class),
                mock(ReplayCacheProvider.class),
                new ExcludingParametersURIComparator()
            );
            validator.validate(context);
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void verifyThatPartialLogoutAsSecondLevelStatusCodeIsAcceptedAsSuccess() throws Exception {

        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" "+
            "Destination=\"http://sp.example.com/demo1/logout\" " +
            "ID=\"_0a59a9e8-1885-4127-84c8-515354c7e29d\" InResponseTo=\"_1981f72063034b65b659cf5dc484e2f01698e96\" " +
            "IssueInstant=\""+ZonedDateTime.now(ZoneOffset.UTC)+"\" Version=\"2.0\">\n" +
            "    <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">urn:some:redacted:issuer</saml:Issuer>\n" +
            "    <samlp:Status>\n" +
            "        <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:Responder\">\n" +
            "            <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:PartialLogout\"/>\n" +
            "        </samlp:StatusCode>\n" +
            "    </samlp:Status>\n" +
            "</samlp:LogoutResponse>";

        validateResponse(xml);
    }

    @Test
    public void verifyThatPartialLogoutAsTopLevelStatusCodeIsAcceptedAsSuccess() throws Exception {

        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" "+
            "Destination=\"http://sp.example.com/demo1/logout\" " +
            "ID=\"_0a59a9e8-1885-4127-84c8-515354c7e29d\" InResponseTo=\"_1981f72063034b65b659cf5dc484e2f01698e96\" " +
            "IssueInstant=\""+ZonedDateTime.now(ZoneOffset.UTC)+"\" Version=\"2.0\">\n" +
            "    <saml:Issuer xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">urn:some:redacted:issuer</saml:Issuer>\n" +
            "    <samlp:Status>\n" +
            "        <samlp:StatusCode Value=\"urn:oasis:names:tc:SAML:2.0:status:PartialLogout\"/>\n" +
            "    </samlp:Status>\n" +
            "</samlp:LogoutResponse>";

        validateResponse(xml);
    }

    private static final String IDP_ENTITY_ID = "http://idp.example.com/metadata.php";

    private static final String NAME_ID = "victim@example.org";

    private static String buildLogoutRequest(final String sessionIndexElement) {
        return "<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" "
            + "xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" "
            + "ID=\"_a1b2c3\" Version=\"2.0\" IssueInstant=\"" + ZonedDateTime.now(ZoneOffset.UTC) + "\">"
            + "  <saml:Issuer>" + IDP_ENTITY_ID + "</saml:Issuer>"
            + "  <saml:NameID>" + NAME_ID + "</saml:NameID>"
            + sessionIndexElement
            + "</samlp:LogoutRequest>";
    }

    private static SAML2MessageContext getLogoutRequestContext(final String xml) {
        val context = new SAML2MessageContext(new CallContext(getMockWebContext(), new MockSessionStore()));
        context.setSaml2Configuration(getSaml2Configuration());

        val samlMessage = new MessageContext();
        samlMessage.setMessage(Configuration.deserializeSamlObject(xml).orElseThrow());
        context.setMessageContext(samlMessage);
        context.getSAMLPeerMetadataContext().setEntityDescriptor(new EntityDescriptorBuilder().buildObject());
        context.getSAMLPeerEntityContext().setEntityId(IDP_ENTITY_ID);
        return context;
    }

    private static SAML2LogoutValidator getLogoutValidator(final SessionLogoutHandler logoutHandler) {
        return new SAML2LogoutValidator(
            mock(SAML2SignatureTrustEngineProvider.class),
            mock(Decrypter.class),
            logoutHandler,
            mock(ReplayCacheProvider.class),
            new ExcludingParametersURIComparator());
    }

    @Test
    public void verifyUnsignedLogoutRequestWithOnlyANameIdDoesNotDestroyTheSession() {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val context = getLogoutRequestContext(buildLogoutRequest(""));

        assertThrows(SAMLException.class, () -> getLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    @Test
    public void verifyUnsignedLogoutRequestWithASessionIndexDestroysTheSession() {
        val logoutHandler = mock(SessionLogoutHandler.class);
        // the session index is opaque and cannot be guessed: the request is still accepted unsigned
        val context = getLogoutRequestContext(
            buildLogoutRequest("<samlp:SessionIndex>_opaque-session-index</samlp:SessionIndex>"));

        getLogoutValidator(logoutHandler).validate(context);

        verify(logoutHandler).destroySession(any(), eq("_opaque-session-index"));
    }

    @Test
    public void verifyUnsignedLogoutRequestWithABlankSessionIndexDoesNotDestroyTheSession() {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val context = getLogoutRequestContext(buildLogoutRequest("<samlp:SessionIndex></samlp:SessionIndex>"));

        assertThrows(SAMLException.class, () -> getLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    @Test
    public void verifyAuthenticatedLogoutRequestWithOnlyANameIdDestroysTheSession() {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val context = getLogoutRequestContext(buildLogoutRequest(""));
        // set by validateSignatureIfItExists once the signature has been validated against the IdP metadata
        context.getSAMLPeerEntityContext().setAuthenticated(true);

        getLogoutValidator(logoutHandler).validate(context);

        verify(logoutHandler).destroySession(any(), eq(NAME_ID));
    }

    private static final KeyPair IDP_KEYS = generateKeyPair();

    private static KeyPair generateKeyPair() {
        try {
            val generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    // the IdP public key as the only trusted credential
    private static SAML2LogoutValidator getRedirectLogoutValidator(final SessionLogoutHandler logoutHandler) {
        val credential = CredentialSupport.getSimpleCredential(IDP_KEYS.getPublic(), null);
        SAML2SignatureTrustEngineProvider engine = () -> new ExplicitKeySignatureTrustEngine(new StaticCredentialResolver(credential),
            DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());
        return new SAML2LogoutValidator(engine, mock(Decrypter.class), logoutHandler, mock(ReplayCacheProvider.class),
            new ExcludingParametersURIComparator());
    }

    // signs SAMLRequest=...&RelayState=...&SigAlg=... as URL-encoded (SAML Bindings 2.0, §3.4.4.1)
    private static String buildSignedQueryString(final String xml, final String relayState, final PrivateKey key) throws Exception {
        var signedContent = "SAMLRequest=" + URLEncoder.encode(Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8);
        if (relayState != null) {
            signedContent += "&RelayState=" + URLEncoder.encode(relayState, StandardCharsets.UTF_8);
        }
        signedContent += "&SigAlg=" + URLEncoder.encode(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, StandardCharsets.UTF_8);
        val signer = java.security.Signature.getInstance("SHA256withRSA");
        signer.initSign(key);
        signer.update(signedContent.getBytes(StandardCharsets.UTF_8));
        return signedContent + "&Signature=" + URLEncoder.encode(Base64.getEncoder().encodeToString(signer.sign()), StandardCharsets.UTF_8);
    }

    private static SAML2MessageContext getRedirectLogoutRequestContext(final String xml, final String queryString) {
        val context = getLogoutRequestContext(xml);
        ((MockWebContext) context.getCallContext().webContext()).setQueryString(queryString);
        context.getSAMLBindingContext().setBindingUri(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
        return context;
    }

    @Test
    public void verifyRedirectSignedLogoutRequestWithOnlyANameIdDestroysTheSession() throws Exception {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val xml = buildLogoutRequest("");
        val context = getRedirectLogoutRequestContext(xml, buildSignedQueryString(xml, null, IDP_KEYS.getPrivate()));

        getRedirectLogoutValidator(logoutHandler).validate(context);

        assertTrue(context.getSAMLPeerEntityContext().isAuthenticated());
        verify(logoutHandler).destroySession(any(), eq(NAME_ID));
    }

    @Test
    public void verifyRedirectSignedLogoutRequestWithARelayStateDestroysTheSession() throws Exception {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val xml = buildLogoutRequest("");
        val queryString = buildSignedQueryString(xml, "https://sp.example.org/a b", IDP_KEYS.getPrivate());
        val context = getRedirectLogoutRequestContext(xml, queryString);

        getRedirectLogoutValidator(logoutHandler).validate(context);

        verify(logoutHandler).destroySession(any(), eq(NAME_ID));
    }

    @Test
    public void verifyRedirectLogoutRequestSignedByAnotherKeyIsRejected() throws Exception {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val xml = buildLogoutRequest("<samlp:SessionIndex>_opaque-session-index</samlp:SessionIndex>");
        val context = getRedirectLogoutRequestContext(xml, buildSignedQueryString(xml, null, generateKeyPair().getPrivate()));

        assertThrows(SAMLSignatureValidationException.class, () -> getRedirectLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    @Test
    public void verifyRedirectLogoutRequestWithATamperedRelayStateIsRejected() throws Exception {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val xml = buildLogoutRequest("");
        val queryString = buildSignedQueryString(xml, "https://sp.example.org", IDP_KEYS.getPrivate())
            .replace("sp.example.org", "evil.example.org");
        val context = getRedirectLogoutRequestContext(xml, queryString);

        assertThrows(SAMLSignatureValidationException.class, () -> getRedirectLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    @Test
    public void verifyRedirectLogoutRequestWithADuplicateSamlRequestIsRejected() throws Exception {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val xml = buildLogoutRequest("");
        val context = getRedirectLogoutRequestContext(xml,
            "SAMLRequest=other&" + buildSignedQueryString(xml, null, IDP_KEYS.getPrivate()));

        assertThrows(SAMLSignatureValidationException.class, () -> getRedirectLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    @Test
    public void verifyUnsignedRedirectLogoutRequestWithOnlyANameIdDoesNotDestroyTheSession() {
        val logoutHandler = mock(SessionLogoutHandler.class);
        val context = getRedirectLogoutRequestContext(buildLogoutRequest(""), "SAMLRequest=abc&RelayState=xyz");

        assertThrows(SAMLException.class, () -> getRedirectLogoutValidator(logoutHandler).validate(context));

        verify(logoutHandler, never()).destroySession(any(), any());
    }

    private void validateResponse(String xml) {
        val webContext = getMockWebContext();
        val context = getSaml2MessageContext(webContext, xml);
        val validator = new SAML2LogoutValidator(
            getTrustEngine(),
            mock(Decrypter.class),
            mock(SessionLogoutHandler.class),
            mock(ReplayCacheProvider.class),
            new ExcludingParametersURIComparator()
        );

        try {
            validator.validate(context);
            fail("Validation should have failed, because response status PartialLogout is not accepted as success by default.");
        }
        catch(SAMLException expectedException) {
            assertNotNull(expectedException);
        }

        validator.setPartialLogoutTreatedAsSuccess(true);
        validator.validate(context);
    }
}
