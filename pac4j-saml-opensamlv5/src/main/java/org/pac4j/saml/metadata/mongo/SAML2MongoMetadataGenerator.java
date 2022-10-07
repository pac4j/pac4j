package org.pac4j.saml.metadata.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.BaseSAML2MetadataGenerator;
import org.pac4j.saml.util.Configuration;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

/**
 * This is {@link SAML2MongoMetadataGenerator}
 * that stores service provider metadata in a MongoDb database.
 *
 * @author Misagh Moayyed
 * @since 5.7.0
 */
public class SAML2MongoMetadataGenerator extends BaseSAML2MetadataGenerator {
    private static final String ID = "saml2-metadata";

    private final MongoClient mongoClient;

    private String metadataDatabase = "saml2";

    private String metadataCollection = "metadata";

    public SAML2MongoMetadataGenerator(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public AbstractMetadataResolver createMetadataResolver() throws Exception {
        var documents = Objects.requireNonNull(getCollection().find(buildMetadataDocumentFilter()));
        var foundDoc = documents.first();
        if (foundDoc != null) {
            var metadata = foundDoc.getString("metadata");
            try (var is = new ByteArrayInputStream(metadata.getBytes(StandardCharsets.UTF_8))) {
                var document = Configuration.getParserPool().parse(is);
                var root = document.getDocumentElement();
                return new DOMMetadataResolver(root);
            }
        }
        throw new SAMLException("Unable to locate metadata document " + ID);
    }

    protected Bson buildMetadataDocumentFilter() {
        return eq("id", ID);
    }

    @Override
    public boolean storeMetadata(final String metadata, final boolean force) {
        if (CommonHelper.isBlank(metadata)) {
            logger.info("No metadata is provided");
            return false;
        }

        var metadataToUse = isSignMetadata() ? getMetadataSigner().sign(metadata) : metadata;
        CommonHelper.assertNotBlank("metadata", metadataToUse);

        var filter = buildMetadataDocumentFilter();
        var foundDoc = getCollection().find(filter).first();
        if (foundDoc == null) {
            final var doc = new Document();
            doc.put("metadata", metadataToUse);
            doc.put("id", ID);
            return getCollection().insertOne(doc).getInsertedId() != null;
        }
        foundDoc.put("metadata", metadataToUse);
        var updateResult = getCollection().updateOne(filter, new Document("$set", foundDoc));
        return updateResult.getMatchedCount() == 1;
    }

    protected MongoCollection<Document> getCollection() {
        final var db = mongoClient.getDatabase(metadataDatabase);
        return Objects.requireNonNull(db.getCollection(metadataCollection));
    }

    public String getMetadataDatabase() {
        return metadataDatabase;
    }

    public void setMetadataDatabase(String metadataDatabase) {
        this.metadataDatabase = metadataDatabase;
    }

    public String getMetadataCollection() {
        return metadataCollection;
    }

    public void setMetadataCollection(String metadataCollection) {
        this.metadataCollection = metadataCollection;
    }
}
