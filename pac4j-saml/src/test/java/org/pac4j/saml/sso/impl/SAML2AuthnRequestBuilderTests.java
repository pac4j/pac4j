package org.pac4j.saml.sso.impl;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.saml.client.AbstractSAML2ClientTests;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;
import org.pac4j.saml.util.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * This is {@link SAML2AuthnRequestBuilderTests}.
 *
 * @author Misagh Moayyed
 * @since 4.0.0
 */
public class SAML2AuthnRequestBuilderTests extends AbstractSAML2ClientTests {
    private SAML2Configuration configuration;

    @Before
    public void setup() {
        configuration = getSaml2Configuration();
        configuration.setAssertionConsumerServiceIndex(1);

        var scopedIdP = new SAML2ScopingIdentityProvider("idp-entity-id", "My Identity Provider");
        configuration.getScopingIdentityProviders().add(scopedIdP);
    }

    @Test
    public void testHttpSessionStoreGetterAndSetter() throws Exception {
//        final WebContext webContext = MockWebContext.create();
//
//        val messageStoreFactory = configuration.getSamlMessageStoreFactory();
//        val store = messageStoreFactory.getMessageStore(webContext, new MockSessionStore());
//
//        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();

//        val authnRequest = builder.build(context);
//        authnRequest.setAssertionConsumerServiceURL("https://pac4j.org");
//
//        store.set(authnRequest.getID(), authnRequest);
//
//        assertNotNull(store.get(authnRequest.getID()));
//        assertEquals("https://pac4j.org", authnRequest.getAssertionConsumerServiceURL());


        var logoutRequest = (LogoutRequest) Configuration.getBuilderFactory()
            .getBuilder(LogoutRequest.DEFAULT_ELEMENT_NAME)
            .buildObject(LogoutRequest.DEFAULT_ELEMENT_NAME);

        logoutRequest.setID("23hgbcehfgeb7843jdv1");
        val issuer =  (Issuer) Configuration.getBuilderFactory()
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME)
            .buildObject(Issuer.DEFAULT_ELEMENT_NAME);

        issuer.setValue("https://samltest.id/saml/sp");

        var n = (NameID) Configuration.getBuilderFactory()
            .getBuilder(NameID.DEFAULT_ELEMENT_NAME)
            .buildObject(NameID.DEFAULT_ELEMENT_NAME);
        n.setValue(UUID.randomUUID().toString());
        logoutRequest.setNameID(n);

        var s = (SessionIndex) Configuration.getBuilderFactory()
            .getBuilder(SessionIndex.DEFAULT_ELEMENT_NAME)
            .buildObject(SessionIndex.DEFAULT_ELEMENT_NAME);
        s.setValue(UUID.randomUUID().toString());
        logoutRequest.getSessionIndexes().add(s);

        logoutRequest.setIssuer(issuer);
        logoutRequest.setDestination("https://localhost:8443/cas/idp/profile/SAML2/POST/SLO");
        logoutRequest.setIssueInstant(Instant.now(Clock.systemUTC()).plus(Duration.ofDays(365)));

        new SAML2Client(configuration).getLogoutRequestMessageSender().sendMessage(context, logoutRequest, "");
    }

    private SAML2MessageContext buildContext() {
        val acs = mock(AssertionConsumerService.class);
        when(acs.getLocation()).thenReturn("https://pac4j.org");
        when(acs.getIndex()).thenReturn(configuration.getAssertionConsumerServiceIndex());

        val ssoService = mock(SingleSignOnService.class);
        when(ssoService.getBinding()).thenReturn(getSaml2Configuration().getAuthnRequestBindingType());

        val idpDescriptor = mock(IDPSSODescriptor.class);
        when(idpDescriptor.getSingleSignOnServices()).thenReturn(Collections.singletonList(ssoService));

        val spDescriptor = mock(SPSSODescriptor.class);
        when(spDescriptor.getAssertionConsumerServices()).thenReturn(Collections.singletonList(acs));

        val context = new SAML2MessageContext(new CallContext(MockWebContext.create(), new MockSessionStore()));
        context.getSAMLPeerMetadataContext().setRoleDescriptor(idpDescriptor);
        context.getSAMLSelfMetadataContext().setRoleDescriptor(spDescriptor);
        context.getSAMLSelfEntityContext().setEntityId("entity-id");
        context.setSaml2Configuration(configuration);
        return context;
    }

    @Test
    public void testBuildAuthnRequestWithNoProviderAndNameIdPolicyAllowCreate() {
        configuration.setProviderName(null);
        configuration.setUseNameQualifier(true);
        configuration.setNameIdPolicyFormat("sample-nameid-format");
        configuration.setNameIdPolicyAllowCreate(null);
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        assertNotNull(builder.build(context));
    }

    @Test
    public void testForceAuthAsRequestAttribute() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        context.getCallContext().webContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN, true);
        assertNotNull(builder.build(context));
    }

    @Test
    public void testPassiveAuthAsRequestAttribute() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        context.getCallContext().webContext().setRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE, true);
        assertNotNull(builder.build(context));
    }

    @Test
    public void testScopingIdentityProviders() {
        SAML2ObjectBuilder<AuthnRequest> builder = new SAML2AuthnRequestBuilder();
        val context = buildContext();
        var authnRequest = builder.build(context);
        assertNotNull(authnRequest);
        assertNotNull(authnRequest.getScoping());
    }

    @Override
    protected String getCallbackUrl() {
        return "http://localhost:8080/callback?client_name=" + SAML2Client.class.getSimpleName();
    }

    @Override
    protected String getAuthnRequestBindingType() {
        return SAMLConstants.SAML2_POST_BINDING_URI;
    }
}
