package org.pac4j.mongo.profile.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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

import static com.mongodb.client.model.Filters.*;

/**
 * The MongoDB profile service (which supersedes the Mongo authenticator).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class MongoProfileService extends AbstractProfileService<MongoProfile> {

    private MongoClient mongoClient;

    private String usersDatabase = "users";
    private String usersCollection = "users";

    public MongoProfileService() {}

    public MongoProfileService(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoProfileService(final MongoClient mongoClient, final String attributes) {
        this.mongoClient = mongoClient;
        setAttributes(attributes);
    }

    public MongoProfileService(final MongoClient mongoClient, final String attributes, final PasswordEncoder passwordEncoder) {
        this.mongoClient = mongoClient;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    public MongoProfileService(final MongoClient mongoClient, final PasswordEncoder passwordEncoder) {
        this.mongoClient = mongoClient;
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("passwordEncoder", getPasswordEncoder());
        CommonHelper.assertNotNull("mongoClient", this.mongoClient);
        CommonHelper.assertNotBlank("usersDatabase", this.usersDatabase);
        CommonHelper.assertNotBlank("usersCollection", this.usersCollection);

        defaultProfileDefinition(new CommonProfileDefinition(x -> new MongoProfile()));
        setSerializer(new JsonSerializer(MongoProfile.class));

        super.internalInit();
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        final Document doc = new Document();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            doc.append(entry.getKey(), entry.getValue());
        }

        logger.debug("Insert doc: {}", doc);
        getCollection().insertOne(doc);
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        String id = null;
        final Document doc = new Document();
        for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
            final String name = entry.getKey();
            final Object value = entry.getValue();
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

    @Override
    protected void deleteById(final String id) {

        logger.debug("Delete id: {}", id);
        getCollection().deleteOne(eq(getIdAttribute(), id));
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {

        logger.debug("Reading key / value: {} / {}", key, value);
        final List<Map<String, Object>> listAttributes = new ArrayList<>();
        try (MongoCursor<Document> cursor = getCollection().find(eq(key, value)).iterator()) {
            int i = 0;
            while (cursor.hasNext() && i <= 2) {
                final Document result = cursor.next();
                final Map<String, Object> newAttributes = new HashMap<>();
                // filter on names
                for (final Map.Entry<String, Object> entry : result.entrySet()) {
                    final String name = entry.getKey();
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

    protected MongoCollection<Document> getCollection() {
        final MongoDatabase db = mongoClient.getDatabase(usersDatabase);
        return db.getCollection(usersCollection);
    }

    public String getUsersDatabase() {
        return usersDatabase;
    }

    public void setUsersDatabase(final String usersDatabase) {
        this.usersDatabase = usersDatabase;
    }

    public String getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(final String usersCollection) {
        this.usersCollection = usersCollection;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "mongoClient", mongoClient, "usersCollection", usersCollection,
                "passwordEncoder", getPasswordEncoder(), "usersDatabase", usersDatabase, "attributes", getAttributes(),
                "profileDefinition", getProfileDefinition(), "idAttribute", getIdAttribute(),
                "usernameAttribute", getUsernameAttribute(), "passwordAttribute", getPasswordAttribute());
    }
}
