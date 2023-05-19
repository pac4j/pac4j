package org.pac4j.config.builder;

import lombok.val;
import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.*;

import java.util.Collection;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for OAuth clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuthBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for OAuthBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public OAuthBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreateLinkedInClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateLinkedInClient(final Collection<Client> clients) {
        val id = getProperty(LINKEDIN_ID);
        val secret = getProperty(LINKEDIN_SECRET);
        val scope = getProperty(LINKEDIN_SCOPE);

        if (isNotBlank(id) && isNotBlank(secret)) {
            val linkedInClient = new LinkedIn2Client(id, secret);
            if (isNotBlank(scope)) {
                linkedInClient.setScope(scope);
            }
            clients.add(linkedInClient);
        }
    }

    /**
     * <p>tryCreateFacebookClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateFacebookClient(final Collection<Client> clients) {
        val id = getProperty(FACEBOOK_ID);
        val secret = getProperty(FACEBOOK_SECRET);
        val scope = getProperty(FACEBOOK_SCOPE);
        val fields = getProperty(FACEBOOK_FIELDS);
        if (isNotBlank(id) && isNotBlank(secret)) {
            val facebookClient = new FacebookClient(id, secret);
            if (isNotBlank(scope)) {
                facebookClient.setScope(scope);
            }
            if (isNotBlank(fields)) {
                facebookClient.setFields(fields);
            }
            clients.add(facebookClient);
        }
    }

    /**
     * <p>tryCreateWindowsLiveClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateWindowsLiveClient(final Collection<Client> clients) {
        val id = getProperty(WINDOWSLIVE_ID);
        val secret = getProperty(WINDOWSLIVE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client client = new WindowsLiveClient(id, secret);
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateFoursquareClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateFoursquareClient(final Collection<Client> clients) {
        val id = getProperty(FOURSQUARE_ID);
        val secret = getProperty(FOURSQUARE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client client = new FoursquareClient(id, secret);
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateGoogleClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateGoogleClient(final Collection<Client> clients) {
        val id = getProperty(GOOGLE_ID);
        val secret = getProperty(GOOGLE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            val client = new Google2Client(id, secret);
            val scope = getProperty(GOOGLE_SCOPE);
            if (isNotBlank(scope)) {
                client.setScope(Google2Client.Google2Scope.valueOf(scope.toUpperCase()));
            }
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateYahooClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateYahooClient(final Collection<Client> clients) {
        val id = getProperty(YAHOO_ID);
        val secret = getProperty(YAHOO_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client client = new YahooClient(id, secret);
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateDropboxClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateDropboxClient(final Collection<Client> clients) {
        val id = getProperty(DROPBOX_ID);
        val secret = getProperty(DROPBOX_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client client = new DropBoxClient(id, secret);
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateGithubClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateGithubClient(final Collection<Client> clients) {
        val id = getProperty(GITHUB_ID);
        val secret = getProperty(GITHUB_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client client = new GitHubClient(id, secret);
            clients.add(client);
        }
    }

    /**
     * <p>tryCreateTwitterClient.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateTwitterClient(final Collection<Client> clients) {
        val id = getProperty(TWITTER_ID);
        val secret = getProperty(TWITTER_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            Client twitterClient = new TwitterClient(id, secret);
            clients.add(twitterClient);
        }
    }

    /**
     * <p>tryCreateGenericOAuth2Clients.</p>
     *
     * @param clients a {@link java.util.List} object
     */
    public void tryCreateGenericOAuth2Clients(final Collection<Client> clients) {
        for (var i = 0; i <= MAX_NUM_CLIENTS; i++) {
            val id = getProperty(OAUTH2_ID, i);
            val secret = getProperty(OAUTH2_SECRET, i);

            if (isNotBlank(id) && isNotBlank(secret)) {
                val client = new GenericOAuth20Client();
                client.setName(concat(client.getName(), i));

                client.setKey(id);
                client.setSecret(secret);

                client.setAuthUrl(getProperty(OAUTH2_AUTH_URL, i));
                client.setTokenUrl(getProperty(OAUTH2_TOKEN_URL, i));
                client.setProfileUrl(getProperty(OAUTH2_PROFILE_URL, i));
                client.setProfilePath(getProperty(OAUTH2_PROFILE_PATH, i));
                client.setProfileId(getProperty(OAUTH2_PROFILE_ID, i));
                client.setScope(getProperty(OAUTH2_SCOPE, i));

                if (containsProperty(OAUTH2_WITH_STATE, i)) {
                    client.setWithState(getPropertyAsBoolean(OAUTH2_WITH_STATE, i));
                }
                if (containsProperty(OAUTH2_CLIENT_AUTHENTICATION_METHOD, i)) {
                    client.setClientAuthenticationMethod(getProperty(OAUTH2_CLIENT_AUTHENTICATION_METHOD, i));
                }

                clients.add(client);
            }
        }
    }
}
