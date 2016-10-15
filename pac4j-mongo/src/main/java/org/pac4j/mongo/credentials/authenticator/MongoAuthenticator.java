package org.pac4j.mongo.credentials.authenticator;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.mongo.profile.MongoProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.*;

/**
 * Authenticator for users stored in a MongoDB database, based on the {@link MongoClient} class from the Java Mongo driver.
 * It creates the user profile and stores it in the credentials for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class MongoAuthenticator extends AbstractUsernamePasswordAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected MongoClient mongoClient;

    /**
     * This must a list of attribute names separated by commas.
     */
    protected String attributes = "";
    protected String usernameAttribute = Pac4jConstants.USERNAME;
    protected String passwordAttribute = Pac4jConstants.PASSWORD;
    protected String usersDatabase = "users";
    protected String usersCollection = "users";

    public MongoAuthenticator() {}

    public MongoAuthenticator(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoAuthenticator(final MongoClient mongoClient, final String attributes) {
        this.mongoClient = mongoClient;
        this.attributes = attributes;
    }

    public MongoAuthenticator(final MongoClient mongoClient, final String attributes, final PasswordEncoder passwordEncoder) {
        this.mongoClient = mongoClient;
        this.attributes = attributes;
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("mongoClient", this.mongoClient);
        CommonHelper.assertNotNull("usernameAttribute", this.usernameAttribute);
        CommonHelper.assertNotNull("passwordAttribute", this.passwordAttribute);
        CommonHelper.assertNotNull("usersDatabase", this.usersDatabase);
        CommonHelper.assertNotNull("usersCollection", this.usersCollection);
        CommonHelper.assertNotNull("attributes", this.attributes);

        super.internalInit(context);
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction {

        init(context);

        final String username = credentials.getUsername();

        final MongoDatabase db = mongoClient.getDatabase(usersDatabase);
        final MongoCollection<Document> collection = db.getCollection(usersCollection);
        final List<Document> users = new ArrayList<>();
        try (final MongoCursor<Document> cursor = collection.find(eq(usernameAttribute, username)).iterator()) {
            int i= 0;
            while (cursor.hasNext() && i <= 2) {
                users.add(cursor.next());
                i++;
            }
        }
        logger.debug("Fonund {} users for username: {}", users.size(), username);

        if (users.isEmpty()) {
            throw new AccountNotFoundException("No account found for: " + username);
        } else if (users.size() > 1) {
            throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
        } else {
            final Map<String, Object> user = users.get(0);
            final String returnedPassword = (String) user.get(passwordAttribute);
            if (!getPasswordEncoder().matches(credentials.getPassword(), returnedPassword)) {
                throw new BadCredentialsException("Bad credentials for: " + username);
            } else {
                final MongoProfile profile = createProfile(username, attributes.split(","), user);
                credentials.setUserProfile(profile);
            }
        }
    }

    protected MongoProfile createProfile(final String username, final String[] attributes, final Map<String, Object> result) {
        final MongoProfile profile = new MongoProfile();
        profile.setId(username);
        for (String attribute: attributes) {
            profile.addAttribute(attribute, result.get(attribute));
        }
        return profile;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getUsernameAttribute() {
        return usernameAttribute;
    }

    public void setUsernameAttribute(String usernameAttribute) {
        this.usernameAttribute = usernameAttribute;
    }

    public String getPasswordAttribute() {
        return passwordAttribute;
    }

    public void setPasswordAttribute(String passwordAttribute) {
        this.passwordAttribute = passwordAttribute;
    }

    public String getUsersDatabase() {
        return usersDatabase;
    }

    public void setUsersDatabase(String usersDatabase) {
        this.usersDatabase = usersDatabase;
    }

    public String getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(String usersCollection) {
        this.usersCollection = usersCollection;
    }
}
