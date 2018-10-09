package org.pac4j.saml.client;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.saml.metadata.resolver.ChainingMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.SAML2ContextProvider;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.credentials.authenticator.SAML2Authenticator;
import org.pac4j.saml.crypto.CredentialProvider;
import org.pac4j.saml.crypto.DefaultSignatureSigningParametersProvider;
import org.pac4j.saml.crypto.ExplicitSignatureTrustEngineProvider;
import org.pac4j.saml.crypto.KeyStoreCredentialProvider;
import org.pac4j.saml.crypto.KeyStoreDecryptionProvider;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;
import org.pac4j.saml.logout.SAML2LogoutActionBuilder;
import org.pac4j.saml.metadata.SAML2IdentityProviderMetadataResolver;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.metadata.SAML2ServiceProviderMetadataResolver;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.redirect.SAML2RedirectActionBuilder;
import org.pac4j.saml.sso.SAML2ProfileHandler;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.sso.impl.SAML2DefaultResponseValidator;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageReceiver;
import org.pac4j.saml.sso.impl.SAML2WebSSOMessageSender;
import org.pac4j.saml.sso.impl.SAML2WebSSOProfileHandler;
import org.pac4j.saml.state.SAML2StateGenerator;
import org.pac4j.saml.util.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the client to authenticate users with a SAML2 Identity Provider. This implementation relies on the Web
 * Browser SSO profile with HTTP-POST binding. (http://docs.oasis-open.org/security/saml/v2.0/saml-profiles-2.0-os.pdf).
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @author Ruochao Zheng
 * @since 1.5.0
 */
public class SAML2Client extends IndirectClient<SAML2Credentials, SAML2Profile> {

    public static final String IDP_LOGOUT_REQUEST_EXTRA_PARAMETER = "idplogoutrequest";

    protected CredentialProvider credentialProvider;

    protected SAMLContextProvider contextProvider;

    protected SignatureSigningParametersProvider signatureSigningParametersProvider;

    protected SAML2ProfileHandler<AuthnRequest> profileHandler;

    protected SAML2ResponseValidator responseValidator;

    protected SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected SAML2MetadataResolver idpMetadataResolver;

    protected SAML2MetadataResolver spMetadataResolver;

    protected Decrypter decrypter;

    protected SAML2ClientConfiguration configuration;

    protected StateGenerator stateGenerator = new SAML2StateGenerator(this);

    static {
        CommonHelper.assertNotNull("parserPool", Configuration.getParserPool());
        CommonHelper.assertNotNull("marshallerFactory", Configuration.getMarshallerFactory());
        CommonHelper.assertNotNull("unmarshallerFactory", Configuration.getUnmarshallerFactory());
        CommonHelper.assertNotNull("builderFactory", Configuration.getBuilderFactory());
    }

    public SAML2Client() { }

    public SAML2Client(final SAML2ClientConfiguration configuration) {
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

        defaultRedirectActionBuilder(new SAML2RedirectActionBuilder(this));
        defaultCredentialsExtractor(ctx -> {
            final boolean logoutRequest = ctx.getRequestParameter(IDP_LOGOUT_REQUEST_EXTRA_PARAMETER) != null;
            // SAML logout request
            if (logoutRequest) {
                // currently ignored
                logger.info("Not supported: ignoring SAML logout request");
                return null;
            } else {
                // SAML authn response
                final SAML2MessageContext samlContext = this.contextProvider.buildContext(ctx);
                final SAML2Credentials credentials = (SAML2Credentials) this.profileHandler.receive(samlContext);
                return credentials;
            }
        });
        defaultAuthenticator(new SAML2Authenticator(this.configuration.getAttributeAsId()));
        defaultLogoutActionBuilder(new SAML2LogoutActionBuilder<>(this));
    }

    protected void initSAMLProfileHandler() {
        this.profileHandler = new SAML2WebSSOProfileHandler(
                new SAML2WebSSOMessageSender(this.signatureSigningParametersProvider,
                        this.configuration.getAuthnRequestBindingType(),
                        this.configuration.isAuthnRequestSigned()),
                new SAML2WebSSOMessageReceiver(this.responseValidator));
    }

    protected void initSAMLResponseValidator() {
        // Build the SAML response validator
        this.responseValidator = new SAML2DefaultResponseValidator(
                this.signatureTrustEngineProvider,
                this.decrypter,
                this.configuration.getMaximumAuthenticationLifetime(),
                this.configuration.isWantsAssertionsSigned());
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

    public final SAML2ResponseValidator getResponseValidator() {
        return this.responseValidator;
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

    public void setConfiguration(final SAML2ClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public final SAML2ClientConfiguration getConfiguration() {
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
}
