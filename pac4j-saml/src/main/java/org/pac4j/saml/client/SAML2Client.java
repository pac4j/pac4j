package org.pac4j.saml.client;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.credentials.extractor.SAML2CredentialsExtractor;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.DefaultSignatureSigningParametersProvider;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.crypto.KeyStoreDecryptionProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.logout.SAML2LogoutActionBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutMessageReceiver;
import org.pac4j.saml.logout.impl.SAML2LogoutProfileHandler;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestMessageSender;
import org.pac4j.saml.logout.impl.SAML2LogoutValidator;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.redirect.SAML2RedirectActionBuilder;
import org.pac4j.saml.profile.api.SAML2ProfileHandler;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.sso.impl.*;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.pac4j.saml.util.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the client to authenticate and logout users with a SAML2 Identity Provider.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SAML2Client extends IndirectClient<SAML2Credentials> {

    protected CredentialProvider credentialProvider;

    protected SAMLContextProvider contextProvider;

    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    protected SAML2ProfileHandler<AuthnRequest> profileHandler;

    protected SAML2ProfileHandler<LogoutRequest> logoutProfileHandler;

    protected SAML2ResponseValidator authnResponseValidator;

    protected SAML2ResponseValidator logoutValidator;

    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected SAML2MetadataResolver idpMetadataResolver;

    protected SAML2MetadataResolver spMetadataResolver;

    protected Decrypter decrypter;

    protected SAML2Configuration configuration;

    protected StateGenerator stateGenerator = new SAML2StateGenerator(this);

    static {
        CommonHelper.assertNotNull("parserPool", Configuration.getParserPool());
        CommonHelper.assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        CommonHelper.assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        CommonHelper.assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

    public SAML2Client() { }

    public SAML2Client(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotNull("configuration", this.configuration);

        // First of all, initialize the configuration. It may dynamically load some properties, if it is not a static one.
        this.configuration.init(getName());

        initCredentialProvider();
        initDecrypter();
        initSignatureSigningParametersProvider();
        final MetadataResolver metadataManager = initChainingMetadataResolver(
                initIdentityProviderMetadataResolver(),
                initServiceProviderMetadataResolver());
        initSAMLContextProvider(metadataManager);
        initSignatureTrustEngineProvider(metadataManager);
        initSAMLResponseValidator();
        initSAMLProfileHandler();
        initSAMLLogoutResponseValidator();
        initSAMLLogoutProfileHandler();

        defaultRedirectActionBuilder(new SAML2RedirectActionBuilder(this));
        defaultCredentialsExtractor(new SAML2CredentialsExtractor(this));
        defaultAuthenticator(new SAML2Authenticator(this.configuration.getAttributeAsId(), this.configuration.getMappedAttributes()));
        defaultLogoutActionBuilder(new SAML2LogoutActionBuilder(this));
    }

    protected void initSAMLProfileHandler() {
        this.profileHandler = new SAML2WebSSOProfileHandler(
                new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider,
                        this.configuration.getAuthnRequestBindingType(),
                        true,
                        this.configuration.isAuthnRequestSigned()),
                new SAML2WebSSOMessageReceiver(this.authnResponseValidator));
    }

    protected void initSAMLLogoutProfileHandler() {
        this.logoutProfileHandler = new SAML2LogoutProfileHandler(
            new SAML2LogoutRequestMessageSender(this.signatureSigningParametersProvider,
                this.configuration.getSpLogoutRequestBindingType(), false, this.configuration.isSpLogoutRequestSigned()),
            new SAML2LogoutMessageReceiver(this.logoutValidator));
    }

    protected void initSAMLLogoutResponseValidator() {
        this.logoutValidator = new SAML2LogoutValidator(this.signatureTrustEngineProvider,
            this.decrypter, this.configuration.getLogoutHandler());
        this.logoutValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
    }

    protected void initSAMLResponseValidator() {
        // Build the SAML response validator
        this.authnResponseValidator = new SAML2AuthnResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.configuration.getLogoutHandler(),
                this.configuration.getMaximumAuthenticationLifetime(),
                this.configuration.isWantsAssertionsSigned());
        this.authnResponseValidator.setAcceptedSkew(this.configuration.getAcceptedSkew());
    }

    protected void initSignatureTrustEngineProvider(final MetadataResolver metadataManager) {
        // Build provider for digital signature validation and encryption
        this.signatureTrustEngineProvider = new ExplicitSignatureTrustEngineProvider(metadataManager);
    }

    protected void initSAMLContextProvider(final MetadataResolver metadataManager) {
        // Build the contextProvider
        this.contextProvider = new SAML2ContextProvider(metadataManager,
                this.idpMetadataResolver, this.spMetadataResolver,
                this.configuration.getSamlMessageStorageFactory());
    }

    protected MetadataResolver initServiceProviderMetadataResolver() {
        this.spMetadataResolver = new SAML2ServiceProviderMetadataResolver(this.configuration,
            computeFinalCallbackUrl(null),
            this.credentialProvider);
        return this.spMetadataResolver.resolve();
    }

    protected MetadataResolver initIdentityProviderMetadataResolver() {
        this.idpMetadataResolver = new SAML2IdentityProviderMetadataResolver(this.configuration);
        return this.idpMetadataResolver.resolve();
    }

    protected void initCredentialProvider() {
        this.credentialProvider = new KeyStoreCredentialProvider(this.configuration);
    }

    protected void initDecrypter() {
        this.decrypter = new KeyStoreDecryptionProvider(this.credentialProvider).build();
    }

    protected void initSignatureSigningParametersProvider() {
        this.signatureSigningParametersProvider = new DefaultSignatureSigningParametersProvider(
                this.credentialProvider, this.configuration);
    }

    protected ChainingMetadataResolver initChainingMetadataResolver(final MetadataResolver idpMetadataProvider,
                                                                          final MetadataResolver spMetadataProvider) {
        final ChainingMetadataResolver metadataManager = new ChainingMetadataResolver();
        metadataManager.setId(ChainingMetadataResolver.class.getCanonicalName());
        try {
            final List<MetadataResolver> list = new ArrayList<>();
            list.add(idpMetadataProvider);
            list.add(spMetadataProvider);
            metadataManager.setResolvers(list);
            metadataManager.initialize();
        } catch (final ResolverException e) {
            throw new TechnicalException("Error adding idp or sp metadatas to manager", e);
        } catch (final ComponentInitializationException e) {
            throw new TechnicalException("Error initializing manager", e);
        }
        return metadataManager;
    }

    @Override
    public void notifySessionRenewal(final String oldSessionId, final WebContext context) {
        configuration.findLogoutHandler().renewSession(oldSessionId, context);
    }

    public SAML2ResponseValidator getAuthnResponseValidator() {
        return this.authnResponseValidator;
    }

    public final SAML2MetadataResolver getServiceProviderMetadataResolver() {
        return this.spMetadataResolver;
    }

    public final SAML2MetadataResolver getIdentityProviderMetadataResolver() {
        return this.idpMetadataResolver;
    }

    public final String getIdentityProviderResolvedEntityId() {
        return this.idpMetadataResolver.getEntityId();
    }

    public final String getServiceProviderResolvedEntityId() {
        return this.spMetadataResolver.getEntityId();
    }

    public void setConfiguration(final SAML2Configuration configuration) {
        this.configuration = configuration;
    }

    public final SAML2Configuration getConfiguration() {
        return this.configuration;
    }

    public SAMLContextProvider getContextProvider() {
        return contextProvider;
    }

    public SAML2ProfileHandler<AuthnRequest> getProfileHandler() {
        return profileHandler;
    }

    public SignatureSigningParametersProvider getSignatureSigningParametersProvider() {
        return signatureSigningParametersProvider;
    }

    public SAML2SignatureTrustEngineProvider getSignatureTrustEngineProvider() {
        return signatureTrustEngineProvider;
    }

    public StateGenerator getStateGenerator() {
        return stateGenerator;
    }

    public void setStateGenerator(final StateGenerator stateGenerator) {
        CommonHelper.assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }

    public SAML2ProfileHandler<LogoutRequest> getLogoutProfileHandler() {
        return logoutProfileHandler;
    }

    public void setLogoutProfileHandler(final SAML2ProfileHandler<LogoutRequest> logoutProfileHandler) {
        this.logoutProfileHandler = logoutProfileHandler;
    }
}
