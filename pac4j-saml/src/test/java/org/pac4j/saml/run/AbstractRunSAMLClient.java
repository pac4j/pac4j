package org.pac4j.saml.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.profile.SAML2Profile;
import org.pac4j.saml.storage.HttpSessionStorageFactory;

import java.io.File;

/**
 * This is {@link AbstractRunSAMLClient}.
 *
 * @author Misagh Moayyed
 */
public abstract class AbstractRunSAMLClient extends RunClient {
    
    private static final String IDP_METDATA_LOCATION = "https://mmoayyed.unicon.net:8443/cas/idp/metadata";
    
    @Override
    protected String getLogin() {
        return "casuser";
    }

    @Override
    protected String getPassword() {
        return "Mellon";
    }

    @Override
    protected IndirectClient getClient() {
        final SAML2ClientConfiguration cfg =
                new SAML2ClientConfiguration("resource:samlKeystore.jks",
                        "pac4j-demo-passwd",
                        "pac4j-demo-passwd",
                        IDP_METDATA_LOCATION);
        cfg.setMaximumAuthenticationLifetime(3600);
        cfg.setDestinationBindingType(getDestinationBindingType());
        cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
        cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());
        cfg.setSamlMessageStorageFactory(new HttpSessionStorageFactory());
        final SAML2Client saml2Client = new SAML2Client(cfg);
        saml2Client.setCallbackUrl(getCallbackUrl());
        return saml2Client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(SAML2Profile.class);
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {

    }

    protected abstract String getCallbackUrl();

    protected abstract String getDestinationBindingType();
}
