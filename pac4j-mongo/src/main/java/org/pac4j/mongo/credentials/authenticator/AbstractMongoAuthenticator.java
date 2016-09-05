package org.pac4j.mongo.credentials.authenticator;

import java.util.Iterator;

import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.exception.AccountNotFoundException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.MultipleAccountsFoundException;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.mongo.profile.MongoProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authenticator for users stored in a MongoDB database. It creates the user profile and stores it in the credentials
 * for the {@link AuthenticatorProfileCreator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class AbstractMongoAuthenticator<TDoc> extends AbstractUsernamePasswordAuthenticator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * This must a list of attribute names separated by commas.
     */
    protected String attributes = "";
    protected String usernameAttribute = Pac4jConstants.USERNAME;
    protected String passwordAttribute = Pac4jConstants.PASSWORD;
    protected String usersDatabase = "users";
    protected String usersCollection = "users";

    public AbstractMongoAuthenticator() {}

    public AbstractMongoAuthenticator(final String attributes) {
        this.attributes = attributes;
    }

    public AbstractMongoAuthenticator(final String attributes, final PasswordEncoder passwordEncoder) {
        this.attributes = attributes;
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("usernameAttribute", this.usernameAttribute);
        CommonHelper.assertNotNull("passwordAttribute", this.passwordAttribute);
        CommonHelper.assertNotNull("usersDatabase", this.usersDatabase);
        CommonHelper.assertNotNull("usersCollection", this.usersCollection);
        CommonHelper.assertNotNull("attributes", this.attributes);

        super.internalInit(context);
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction {

        final String username = credentials.getUsername();

        final Iterator<TDoc> it = getUsersFor(credentials).iterator();

        if (!it.hasNext()) {
            throw new AccountNotFoundException("No account found for: " + username);
        } else {
            final TDoc user = it.next();
            if (it.hasNext()) {
                throw new MultipleAccountsFoundException("Too many accounts found for: " + username);
            }
            final String expectedPassword = getPasswordEncoder().encode(credentials.getPassword());
            final String returnedPassword = getUserAttribute(user, passwordAttribute);
            if (CommonHelper.areNotEquals(returnedPassword, expectedPassword)) {
                throw new BadCredentialsException("Bad credentials for: " + username);
            } else {
                final MongoProfile profile = createProfile(username, attributes.split(","), user);
                credentials.setUserProfile(profile);
            }
        }
    }

    protected abstract Iterable<TDoc> getUsersFor(UsernamePasswordCredentials credentials);

    protected abstract String getUserAttribute(TDoc user, String attribute);

    protected MongoProfile createProfile(final String username, final String[] attributes, final TDoc result) {
        final MongoProfile profile = new MongoProfile();
        profile.setId(username);
        for (String attribute: attributes) {
            profile.addAttribute(attribute, getUserAttribute(result, attribute));
        }
        return profile;
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
