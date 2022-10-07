package org.pac4j.saml.metadata.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.metadata.SAML2MetadataGenerator;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This is {@link SAML2MongoMetadataGeneratorIT}.
 *
 * @author Misagh Moayyed
 * @since 5.7.0
 */
public class SAML2MongoMetadataGeneratorIT implements TestsConstants {
    private static final int PORT = 37018;
    private static final String ENTITY_ID = "org:pac4j:example";

    private final MongoServer mongoServer = new MongoServer();

    private SAML2MetadataGenerator mongoMetadataGenerator;

    @Before
    public void setUp() {
        mongoServer.start(PORT);
        mongoMetadataGenerator = new SAML2MongoMetadataGenerator(getClient(), ENTITY_ID);
    }

    @After
    public void tearDown() {
        mongoServer.stop();
    }

    private static MongoClient getClient() {
        return MongoClients.create(String.format("mongodb://localhost:%d", PORT));
    }

    @Test
    public void testMetadata() throws Exception {
        var mgr = new DefaultConfigurationManager();
        mgr.configure();

        final var configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderEntityId(ENTITY_ID);
        configuration.setMetadataGenerator(this.mongoMetadataGenerator);
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        var metadataGenerator = configuration.toMetadataGenerator();
        final var entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        final var metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        assertTrue(metadataGenerator.storeMetadata(metadata, true));
        var resolver = metadataGenerator.buildMetadataResolver();
        assertNotNull(resolver);
        var entityDescriptor = resolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(configuration.getServiceProviderEntityId())));
        assertNotNull(entityDescriptor);

        // now update
        assertTrue(metadataGenerator.storeMetadata(metadata, true));
    }

    private static class MongoServer {
        private MongodExecutable mongodExecutable;

        public void start(final int port) {
            var starter = MongodStarter.getDefaultInstance();
            try {
                var mongodConfig = MongodConfig.builder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();

                mongodExecutable = starter.prepare(mongodConfig);
                mongodExecutable.start();
                final var mongo = MongoClients.create(String.format("mongodb://localhost:%d", port));
                final var db = mongo.getDatabase("saml2");
                db.createCollection("metadata");
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void stop() {
            if (mongodExecutable != null) {
                mongodExecutable.stop();
            }
        }
    }
}
