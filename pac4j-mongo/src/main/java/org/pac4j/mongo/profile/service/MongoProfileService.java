package org.pac4j.mongo.profile.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.bson.Document;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.mongo.profile.MongoProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

/**
 * The MongoDB profile service (which supersedes the Mongo authenticator).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MongoProfileService extends AbstractProfileService<MongoProfile> {

    private MongoClient mongoClient;

    private String usersDatabase = "users";
    private String usersCollection = "users";

    /**
     * <p>Constructor for MongoProfileService.</p>
     */
    public MongoProfileService() {}

    /**
     * <p>Constructor for MongoProfileService.</p>
     *
     * @param mongoClient a {@link MongoClient} object
     */
    public MongoProfileService(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    /**
     * <p>Constructor for MongoProfileService.</p>
     *
     * @param mongoClient a {@link MongoClient} object
     * @param attributes a {@link String} object
     */
    public MongoProfileService(final MongoClient mongoClient, final String attributes) {
        this.mongoClient = mongoClient;
        setAttributes(attributes);
    }

    /**
     * <p>Constructor for MongoProfileService.</p>
     *
     * @param mongoClient a {@link MongoClient} object
     * @param attributes a {@link String} object
     * @param passwordEncoder a {@link PasswordEncoder} object
     */
    public MongoProfileService(final MongoClient mongoClient, final String attributes, final PasswordEncoder passwordEncoder) {
        this.mongoClient = mongoClient;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    /**
     * <p>Constructor for MongoProfileService.</p>
     *
     * @param mongoClient a {@link MongoClient} object
     * @param passwordEncoder a {@link PasswordEncoder} object
     */
    public MongoProfileService(final MongoClient mongoClient, final PasswordEncoder passwordEncoder) {
        this.mongoClient = mongoClient;
        setPasswordEncoder(passwordEncoder);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
        CommonHelper.assertNotNull("mongoClient", this.mongoClient);
        CommonHelper.assertNotBlank("usersDatabase", this.usersDatabase);
        CommonHelper.assertNotBlank("usersCollection", this.usersCollection);

        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new MongoProfile()));
        setSerializer(new JsonSerializer(MongoProfile.class));

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    protected void insert(final Map<String, Object> attributes) {
        val doc = new Document();
        for (val entry : attributes.entrySet()) {
            doc.append(entry.getKey(), entry.getValue());
        }

        logger.debug("Insert doc: {}", doc);
        getCollection().insertOne(doc);
    }

    /** {@inheritDoc} */
    @Override
    protected void update(final Map<String, Object> attributes) {
        String id = null;
        val doc = new Document();
        for (val entry : attributes.entrySet()) {
            val name = entry.getKey();
            val value = entry.getValue();
            if (getIdAttribute().equals(name)) {
                id = (String) value;
            } else {
                doc.append(entry.getKey(), entry.getValue());
            }
        }

        CommonHelper.assertNotNull(ID, id);
        logger.debug("Updating id: {} with doc: {}", id, doc);
        getCollection().updateOne(eq(getIdAttribute(), id), new Document("$set", doc));
    }

    /** {@inheritDoc} */
    @Override
    protected void deleteById(final String id) {

        logger.debug("Delete id: {}", id);
        getCollection().deleteOne(eq(getIdAttribute(), id));
    }

    /** {@inheritDoc} */
    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {

        logger.debug("Reading key / value: {} / {}", key, value);
        List<Map<String, Object>> listAttributes = new ArrayList<>();
        try (val cursor = getCollection().find(eq(key, value)).iterator()) {
            var i = 0;
            while (cursor.hasNext() && i <= 2) {
                val result = cursor.next();
                Map<String, Object> newAttributes = new HashMap<>();
                // filter on names
                for (val entry : result.entrySet()) {
                    val name = entry.getKey();
                    if (names == null || names.contains(name)) {
                        newAttributes.put(name, entry.getValue());
                    }
                }
                listAttributes.add(newAttributes);
                i++;
            }
        }
        logger.debug("Found: {}", listAttributes);

        return listAttributes;
    }

    /**
     * <p>getCollection.</p>
     *
     * @return a {@link MongoCollection} object
     */
    protected MongoCollection<Document> getCollection() {
        val db = mongoClient.getDatabase(usersDatabase);
        return db.getCollection(usersCollection);
    }
}
