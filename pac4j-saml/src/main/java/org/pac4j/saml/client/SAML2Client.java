package org.pac4j.saml.client;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;
import org.pac4j.saml.credentials.extractor.SAML2CredentialsExtractor;
import org.pac4j.saml.crypto.*;
import org.pac4j.saml.logout.SAML2LogoutActionBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestMessageSender;
import org.pac4j.saml.logout.impl.SAML2LogoutValidator;
import org.pac4j.saml.logout.processor.SAML2LogoutProcessor;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.redirect.SAML2RedirectionActionBuilder;
import org.pac4j.saml.replay.InMemoryReplayCacheProvider;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.sso.artifact.DefaultSOAPPipelineProvider;
import org.pac4j.saml.sso.artifact.SOAPPipelineProvider;
import org.pac4j.saml.sso.impl.SAML2AuthnResponseValidator;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageSender;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.pac4j.saml.util.Configuration;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * This class is the client to authenticate and logout users with a SAML2 Identity Provider.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SAML2Client extends IndirectClient {

    @Getter
    protected SAMLContextProvider contextProvider;

    @Getter
    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    @Getter
    protected SAML2ResponseValidator authnResponseValidator;

    @Getter
    protected SAML2LogoutValidator logoutValidator;

    @Getter
    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    @Getter
    protected SAML2MetadataResolver identityProviderMetadataResolver;

    @Getter
    protected SAML2MetadataResolver serviceProviderMetadataResolver;

    protected Decrypter decrypter;

    @Getter
    @Setter
    protected SAML2Configuration configuration;

    @Getter
    @Setter
    protected ValueGenerator stateGenerator = new SAML2StateGenerator(this);

    @Getter
    protected ReplayCacheProvider replayCache;

    protected SOAPPipelineProvider soapPipelineProvider;

    static {
        assertNotNull("parserPool", Configuration.getParserPool());
        assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

    public SAML2Client() { }

    public SAML2Client(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", this.configuration);

        // First of all, initialize the configuration. It may dynamically load some properties, if it is not a static one.
        val callbackUrl = computeFinalCallbackUrl(null);
        configuration.setCallbackUrl(callbackUrl);
        configuration.init(forceReinit);

        initDecrypter();
        initSignatureSigningParametersProvider();
        initIdentityProviderMetadataResolver();
        initServiceProviderMetadataResolver();
        initSAMLContextProvider();
        initSignatureTrustEngineProvider();
        initSAMLReplayCache();
        initSAMLResponseValidator();
        initSOAPPipelineProvider();
        initSAMLLogoutResponseValidator();

        setRedirectionActionBuilderIfUndefined(new SAML2RedirectionActionBuilder(this));
        setCredentialsExtractorIfUndefined(new SAML2CredentialsExtractor(this, this.identityProviderMetadataResolver,
            this.serviceProviderMetadataResolver, this.soapPipelineProvider));
        setAuthenticatorIfUndefined(new SAML2Authenticator(authnResponseValidator, this.logoutValidator,
            this.configuration.getAttributeAsId(), this.configuration.getMappedAttributes()));
        setLogoutProcessor(new SAML2LogoutProcessor(this));
        setLogoutActionBuilderIfUndefined(new SAML2LogoutActionBuilder(this));
    }

    protected void initSOAPPipelineProvider() {
        this.soapPipelineProvider = new DefaultSOAPPipelineProvider(this);
    }

    public SAML2LogoutRequestMessageSender getLogoutRequestMessageSender() {
        return new SAML2LogoutRequestMessageSender(this.signatureSigningParametersProvider,
            this.configuration.getSpLogoutRequestBindingType(), false,
            this.configuration.isSpLogoutRequestSigned());
    }

    public SAML2WebSSOMessageSender getSSOMessageSender() {
        return new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider,
            this.configuration.getAuthnRequestBindingType(), true,
            this.configuration.isAuthnRequestSigned());
    }

    protected void initSAMLLogoutResponseValidator() {
        this.logoutValidator = new SAML2LogoutValidator(this.signatureTrustEngineProvider,
            this.decrypter, this.configuration.getSessionLogoutHandler(),
            this.replayCache, this.configuration.getUriComparator());
        this.logoutValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
        this.logoutValidator.setPartialLogoutTreatedAsSuccess(this.configuration.isPartialLogoutTreatedAsSuccess());
    }

    protected void initSAMLResponseValidator() {
        // Build the SAML response validator
        this.authnResponseValidator = new SAML2AuthnResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.replayCache,
                this.configuration);
        this.authnResponseValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
    }

    protected void initSignatureTrustEngineProvider() {
        // Build provider for digital signature validation and encryption
        this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(
            this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver);
        if (this.configuration.isAllSignatureValidationDisabled()) {
            this.signatureTrustEngineProvider = new LogOnlySignatureTrustEngineProvider(this.signatureTrustEngineProvider);
        }
    }

    protected void initSAMLContextProvider() {
        // Build the contextProvider
        this.contextProvider = new SAML2ContextProvider(this.identityProviderMetadataResolver, this.serviceProviderMetadataResolver,
                this.configuration.getSamlMessageStoreFactory());
    }

    protected void initServiceProviderMetadataResolver() {
        this.serviceProviderMetadataResolver = new SAML2ServiceProviderMetadataResolver(configuration);
        this.serviceProviderMetadataResolver.resolve();
    }

    protected void initIdentityProviderMetadataResolver() {
        this.identityProviderMetadataResolver = this.configuration.getIdentityProviderMetadataResolver();
    }

    protected void initDecrypter() {
        this.decrypter = new KeyStoreDecryptionProvider(configuration.getCredentialProvider()).build();
    }

    protected void initSignatureSigningParametersProvider() {
        this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(configuration);
    }

    protected void initSAMLReplayCache() {
        replayCache = new InMemoryReplayCacheProvider();
    }

    public void destroy() {
        ((SAML2ServiceProviderMetadataResolver) serviceProviderMetadataResolver).destroy();
    }

    @Override
    public void notifySessionRenewal(final CallContext ctx, final String oldSessionId) {
        configuration.findSessionLogoutHandler().renewSession(ctx, oldSessionId);
    }

    public final String getIdentityProviderResolvedEntityId() {
        return this.identityProviderMetadataResolver.getEntityId();
    }

    public final String getServiceProviderResolvedEntityId() {
        return this.serviceProviderMetadataResolver.getEntityId();
    }

}
