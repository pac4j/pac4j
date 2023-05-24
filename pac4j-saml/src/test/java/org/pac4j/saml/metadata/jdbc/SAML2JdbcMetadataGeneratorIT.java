package org.pac4j.saml.metadata.jdbc;

import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.junit.Before;
import org.junit.Test;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.metadata.SAML2MetadataGenerator;
import org.pac4j.saml.util.ConfigurationManager;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This is {@link SAML2JdbcMetadataGeneratorIT}.
 *
 * @author Misagh Moayyed
 * @since 5.7.0
 */
public class SAML2JdbcMetadataGeneratorIT implements TestsConstants {
    private static final String ENTITY_ID = "org:pac4j:example";

    private SAML2MetadataGenerator jdbcMetadataGenerator;

    @Before
    public void setUp() {
        var dataSource = new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
        var template = new JdbcTemplate(dataSource);
        template.execute("CREATE TABLE sp_metadata (entityId VARCHAR(255), metadata CHARACTER LARGE OBJECT)");
        jdbcMetadataGenerator = new SAML2JdbcMetadataGenerator(template, ENTITY_ID);
    }

    @Test
    public void testMetadata() throws Exception {
        ConfigurationManager mgr = new DefaultConfigurationManager();
        mgr.configure();

        val configuration = new SAML2Configuration();
        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderEntityId(ENTITY_ID);
        configuration.setMetadataGenerator(this.jdbcMetadataGenerator);
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.init();

        var metadataGenerator = configuration.toMetadataGenerator();
        val entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        val metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        assertTrue(metadataGenerator.storeMetadata(metadata, true));
        var resolver = metadataGenerator.buildMetadataResolver();
        assertNotNull(resolver);
        var entityDescriptor = resolver.resolveSingle(new CriteriaSet(new EntityIdCriterion(configuration.getServiceProviderEntityId())));
        assertNotNull(entityDescriptor);

        // now update
        assertTrue(metadataGenerator.storeMetadata(metadata, true));
    }
}
