package org.pac4j.saml.sso.artifact;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.impl.BasicMessageHandlerChain;
import org.opensaml.messaging.handler.impl.CheckExpectedIssuer;
import org.opensaml.messaging.handler.impl.CheckMandatoryAuthentication;
import org.opensaml.messaging.handler.impl.CheckMandatoryIssuer;
import org.opensaml.messaging.handler.impl.SchemaValidateXMLMessage;
import org.opensaml.messaging.pipeline.httpclient.BasicHttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.impl.CheckMessageVersionHandler;
import org.opensaml.saml.common.binding.impl.PopulateSignatureSigningParametersHandler;
import org.opensaml.saml.common.binding.impl.SAMLMetadataLookupHandler;
import org.opensaml.saml.common.binding.impl.SAMLProtocolAndRoleHandler;
import org.opensaml.saml.common.binding.impl.SAMLSOAPDecoderBodyHandler;
import org.opensaml.saml.common.binding.security.impl.CheckAndRecordServerTLSEntityAuthenticationtHandler;
import org.opensaml.saml.common.binding.security.impl.InResponseToSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.MessageLifetimeSecurityHandler;
import org.opensaml.saml.common.binding.security.impl.MessageReplaySecurityHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLOutboundProtocolMessageSigningHandler;
import org.opensaml.saml.common.binding.security.impl.SAMLProtocolMessageXMLSignatureSecurityHandler;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder.SAML1Version;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.binding.decoding.impl.HttpClientResponseSOAP11Decoder;
import org.opensaml.saml.saml2.binding.encoding.impl.HttpClientRequestSOAP11Encoder;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.impl.BasicSignatureValidationParametersResolver;
import org.opensaml.xmlsec.messaging.impl.PopulateSignatureValidationParametersHandler;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.xml.sax.SAXException;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

@SuppressWarnings("unchecked")
public class DefaultSOAPPipelineFactory implements HttpClientMessagePipelineFactory<SAMLObject, SAMLObject> {
    protected final SAML2Configuration configuration;

    protected final SAML2MetadataResolver idpMetadataResolver;

    protected final SAML2MetadataResolver spMetadataResolver;

    protected final SignatureSigningParametersProvider signingParametersProvider;

    protected final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected final ReplayCacheProvider replayCache;

    public DefaultSOAPPipelineFactory(SAML2Configuration configuration, SAML2MetadataResolver idpMetadataResolver,
            SAML2MetadataResolver spMetadataResolver, SignatureSigningParametersProvider signingParametersProvider,
            SAML2SignatureTrustEngineProvider signatureTrustEngineProvider, ReplayCacheProvider replayCache) {
        this.configuration = configuration;
        this.idpMetadataResolver = idpMetadataResolver;
        this.spMetadataResolver = spMetadataResolver;
        this.signingParametersProvider = signingParametersProvider;
        this.signatureTrustEngineProvider = signatureTrustEngineProvider;
        this.replayCache = replayCache;
    }

    protected List<MessageHandler<SAMLObject>> getInboundHandlers() throws ComponentInitializationException {
        List<MessageHandler<SAMLObject>> handlers = new ArrayList<>();
        handlers.add(buildSAMLProtocolAndRoleHandler(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        handlers.add(buildSAMLMetadataLookupHandler(idpMetadataResolver));
        handlers.add(buildSchemaValidateXMLMessage());
        handlers.add(buildCheckMessageVersionHandler());
        handlers.add(buildMessageLifetimeSecurityHandler());
        handlers.add(buildInResponseToSecurityHandler());
        handlers.add(buildMessageReplaySecurityHandler());
        handlers.add(buildCheckMandatoryIssuer());
        handlers.add(buildCheckExpectedIssuer());
        handlers.add(buildPopulateSignatureSigningParametersHandler());
        handlers.add(buildPopulateSignatureValidationParametersHandler());
        handlers.add(buildSAMLProtocolMessageXMLSignatureSecurityHandler());
        handlers.add(buildCheckAndRecordServerTLSEntityAuthenticationtHandler());
        handlers.add(buildCheckMandatoryAuthentication());
        handlers.add(buildSAMLSOAPDecoderBodyHandler());
        return handlers;
    }

    protected List<MessageHandler<SAMLObject>> getOutboundPayloadHandlers() throws ComponentInitializationException {
        List<MessageHandler<SAMLObject>> handlers = new ArrayList<>();
        handlers.add(buildSAMLProtocolAndRoleHandler(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
        handlers.add(buildSAMLMetadataLookupHandler(spMetadataResolver));
        handlers.add(buildPopulateSignatureSigningParametersHandler());
        handlers.add(buildSAMLOutboundProtocolMessageSigningHandler());
        return handlers;
    }

    protected List<MessageHandler<SAMLObject>> getOutboundTransportHandlers() throws ComponentInitializationException {
        return new ArrayList<>();
    }

    protected MessageHandler<SAMLObject> buildSAMLProtocolAndRoleHandler(QName roleName)
            throws ComponentInitializationException {
        SAMLProtocolAndRoleHandler protocolAndRoleHandler = new SAMLProtocolAndRoleHandler();
        protocolAndRoleHandler.setProtocol(SAMLConstants.SAML20P_NS);
        protocolAndRoleHandler.setRole(roleName);
        protocolAndRoleHandler.initialize();
        return protocolAndRoleHandler;
    }

    protected MessageHandler<SAMLObject> buildSAMLMetadataLookupHandler(SAML2MetadataResolver metadataResolver)
            throws ComponentInitializationException {
        PredicateRoleDescriptorResolver roleResolver = new PredicateRoleDescriptorResolver(metadataResolver.resolve());
        roleResolver.initialize();

        SAMLMetadataLookupHandler metadataLookupHandler = new SAMLMetadataLookupHandler();
        metadataLookupHandler.setRoleDescriptorResolver(roleResolver);
        metadataLookupHandler.initialize();
        return metadataLookupHandler;
    }

    protected MessageHandler<SAMLObject> buildSchemaValidateXMLMessage() throws ComponentInitializationException {
        try {
            SchemaValidateXMLMessage<SAMLObject> validateXMLHandler = new SchemaValidateXMLMessage<>(
                    new SAMLSchemaBuilder(SAML1Version.SAML_11).getSAMLSchema());
            validateXMLHandler.initialize();
            return validateXMLHandler;
        } catch (SAXException e) {
            throw new ComponentInitializationException(e);
        }
    }

    protected MessageHandler<SAMLObject> buildCheckMessageVersionHandler() throws ComponentInitializationException {
        CheckMessageVersionHandler messageVersionHandler = new CheckMessageVersionHandler();
        messageVersionHandler.initialize();
        return messageVersionHandler;
    }

    protected MessageHandler<SAMLObject> buildMessageLifetimeSecurityHandler() throws ComponentInitializationException {
        MessageLifetimeSecurityHandler lifetimeHandler = new MessageLifetimeSecurityHandler();
        lifetimeHandler.setClockSkew(configuration.getAcceptedSkew() * 1000);
        lifetimeHandler.initialize();
        return lifetimeHandler;
    }

    protected MessageHandler<SAMLObject> buildInResponseToSecurityHandler() throws ComponentInitializationException {
        InResponseToSecurityHandler inResponseToHandler = new InResponseToSecurityHandler();
        inResponseToHandler.initialize();
        return inResponseToHandler;
    }

    protected MessageHandler<SAMLObject> buildMessageReplaySecurityHandler() throws ComponentInitializationException {
        MessageReplaySecurityHandler messageReplayHandler = new MessageReplaySecurityHandler();
        messageReplayHandler.setExpires(configuration.getAcceptedSkew() * 1000);
        messageReplayHandler.setReplayCache(replayCache.get());
        messageReplayHandler.initialize();
        return messageReplayHandler;
    }

    protected MessageHandler<SAMLObject> buildCheckMandatoryIssuer() throws ComponentInitializationException {
        CheckMandatoryIssuer mandatoryIssuer = new CheckMandatoryIssuer();
        mandatoryIssuer.setIssuerLookupStrategy(new IssuerFunction());
        mandatoryIssuer.initialize();
        return mandatoryIssuer;
    }

    protected MessageHandler<SAMLObject> buildCheckExpectedIssuer() throws ComponentInitializationException {
        CheckExpectedIssuer expectedIssuer = new CheckExpectedIssuer();
        expectedIssuer.setIssuerLookupStrategy(new IssuerFunction());
        expectedIssuer.setExpectedIssuerLookupStrategy(messageContext -> idpMetadataResolver.getEntityId());
        expectedIssuer.initialize();
        return expectedIssuer;
    }

    protected MessageHandler<SAMLObject> buildPopulateSignatureSigningParametersHandler()
            throws ComponentInitializationException {
        PopulateSignatureSigningParametersHandler signatureSigningParameters = new PopulateSignatureSigningParametersHandler();
        signatureSigningParameters.setSignatureSigningParametersResolver(
                new DefaultSignatureSigningParametersResolver(signingParametersProvider));
        signatureSigningParameters.initialize();
        return signatureSigningParameters;
    }

    protected MessageHandler<SAMLObject> buildPopulateSignatureValidationParametersHandler()
            throws ComponentInitializationException {
        PopulateSignatureValidationParametersHandler signatureValidationParameters = new PopulateSignatureValidationParametersHandler();
        signatureValidationParameters
                .setSignatureValidationParametersResolver(new BasicSignatureValidationParametersResolver() {
                    @Override
                    protected SignatureTrustEngine resolveSignatureTrustEngine(CriteriaSet criteria) {
                        return signatureTrustEngineProvider.build();
                    }
                });
        signatureValidationParameters.initialize();
        return signatureValidationParameters;
    }

    protected MessageHandler<SAMLObject> buildSAMLProtocolMessageXMLSignatureSecurityHandler()
            throws ComponentInitializationException {
        SAMLProtocolMessageXMLSignatureSecurityHandler messageXMLSignatureHandler = new SAMLProtocolMessageXMLSignatureSecurityHandler();
        messageXMLSignatureHandler.initialize();
        return messageXMLSignatureHandler;
    }

    protected MessageHandler<SAMLObject> buildCheckAndRecordServerTLSEntityAuthenticationtHandler()
            throws ComponentInitializationException {
        CheckAndRecordServerTLSEntityAuthenticationtHandler tlsHandler = new CheckAndRecordServerTLSEntityAuthenticationtHandler();
        tlsHandler.initialize();
        return tlsHandler;
    }

    protected MessageHandler<SAMLObject> buildCheckMandatoryAuthentication() {
        CheckMandatoryAuthentication mandatoryAuthentication = new CheckMandatoryAuthentication();
        mandatoryAuthentication.setAuthenticationLookupStrategy(
            context -> context.getSubcontext(SAMLPeerEntityContext.class).isAuthenticated());
        return mandatoryAuthentication;
    }

    protected MessageHandler<SAMLObject> buildSAMLSOAPDecoderBodyHandler() throws ComponentInitializationException {
        SAMLSOAPDecoderBodyHandler soapDecoderBody = new SAMLSOAPDecoderBodyHandler();
        soapDecoderBody.initialize();
        return soapDecoderBody;
    }

    protected MessageHandler<SAMLObject> buildSAMLOutboundProtocolMessageSigningHandler()
            throws ComponentInitializationException {
        SAMLOutboundProtocolMessageSigningHandler messageSigner = new SAMLOutboundProtocolMessageSigningHandler();
        messageSigner.initialize();
        return messageSigner;
    }

    protected BasicMessageHandlerChain<SAMLObject> toHandlerChain(List<MessageHandler<SAMLObject>> handlers) {
        BasicMessageHandlerChain<SAMLObject> ret = new BasicMessageHandlerChain<>();
        ret.setHandlers(handlers);
        return ret;
    }

    @Override
    @Nonnull
    public HttpClientMessagePipeline<SAMLObject, SAMLObject> newInstance() {
        BasicHttpClientMessagePipeline<SAMLObject, SAMLObject> ret = new BasicHttpClientMessagePipeline<>(
                new HttpClientRequestSOAP11Encoder(), new HttpClientResponseSOAP11Decoder());
        try {
            ret.setInboundHandler(toHandlerChain(getInboundHandlers()));
            ret.setOutboundPayloadHandler(toHandlerChain(getOutboundPayloadHandlers()));
            ret.setOutboundTransportHandler(toHandlerChain(getOutboundTransportHandlers()));
        } catch (ComponentInitializationException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    @Nonnull
    public HttpClientMessagePipeline<SAMLObject, SAMLObject> newInstance(@Nullable String pipelineName) {
        return newInstance();
    }
}
