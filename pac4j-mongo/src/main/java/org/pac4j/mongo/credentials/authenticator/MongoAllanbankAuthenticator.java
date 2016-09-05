package org.pac4j.mongo.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.util.CommonHelper;

import com.allanbank.mongodb.MongoClient;
import com.allanbank.mongodb.MongoCollection;
import com.allanbank.mongodb.MongoDatabase;
import com.allanbank.mongodb.bson.Document;
import com.allanbank.mongodb.bson.Element;
import com.allanbank.mongodb.builder.QueryBuilder;

/**
 * Authenticator for users stored in a MongoDB database, based on the {@link MongoClient} class from the Allanbank
 * Async-based Mongo driver (the synchronous API is used).
 *
 * @author Victor Noël
 * @since 1.9.2
 */
public class MongoAllanbankAuthenticator extends AbstractMongoAuthenticator<Document> {

    protected MongoClient mongoClient;

    public MongoAllanbankAuthenticator() {
    }

    public MongoAllanbankAuthenticator(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoAllanbankAuthenticator(final MongoClient mongoClient, final String attributes) {
        super(attributes);
        this.mongoClient = mongoClient;
    }

    public MongoAllanbankAuthenticator(final MongoClient mongoClient, final String attributes,
            final PasswordEncoder passwordEncoder) {
        super(attributes, passwordEncoder);
        this.mongoClient = mongoClient;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("mongoClient", this.mongoClient);

        super.internalInit(context);
    }

    @Override
    protected Iterable<Document> getUsersFor(UsernamePasswordCredentials credentials) {
        final MongoDatabase db = mongoClient.getDatabase(usersDatabase);
        final MongoCollection collection = db.getCollection(usersCollection);

        return collection.find(QueryBuilder.where(usernameAttribute).equals(credentials.getUsername()));
    }

    @Override
    protected String getUserAttribute(Document user, String attribute) {
        final Element element = user.get(attribute);
        return element == null ? null : element.getValueAsString();
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
}
