package org.pac4j.config.builder;

import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.DropBoxClient;
import org.pac4j.oauth.client.FacebookClient;
import org.pac4j.oauth.client.FoursquareClient;
import org.pac4j.oauth.client.GenericOAuth20Client;
import org.pac4j.oauth.client.GitHubClient;
import org.pac4j.oauth.client.Google2Client;
import org.pac4j.oauth.client.LinkedIn2Client;
import org.pac4j.oauth.client.TwitterClient;
import org.pac4j.oauth.client.WindowsLiveClient;
import org.pac4j.oauth.client.YahooClient;

import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

/**
 * Builder for OAuth clients.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuthBuilder extends AbstractBuilder {

    public OAuthBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateLinkedInClient(final List<Client> clients) {
        final String id = getProperty(LINKEDIN_ID);
        final String secret = getProperty(LINKEDIN_SECRET);
        final String scope = getProperty(LINKEDIN_SCOPE);

        if (isNotBlank(id) && isNotBlank(secret)) {
            final LinkedIn2Client linkedInClient = new LinkedIn2Client(id, secret);
            if (isNotBlank(scope)) {
                linkedInClient.setScope(scope);
            }
            clients.add(linkedInClient);
        }
    }

    public void tryCreateFacebookClient(final List<Client> clients) {
        final String id = getProperty(FACEBOOK_ID);
        final String secret = getProperty(FACEBOOK_SECRET);
        final String scope = getProperty(FACEBOOK_SCOPE);
        final String fields = getProperty(FACEBOOK_FIELDS);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final FacebookClient facebookClient = new FacebookClient(id, secret);
            if (isNotBlank(scope)) {
                facebookClient.setScope(scope);
            }
            if (isNotBlank(fields)) {
                facebookClient.setFields(fields);
            }
            clients.add(facebookClient);
        }
    }

    public void tryCreateWindowsLiveClient(final List<Client> clients) {
        final String id = getProperty(WINDOWSLIVE_ID);
        final String secret = getProperty(WINDOWSLIVE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final WindowsLiveClient client = new WindowsLiveClient(id, secret);
            clients.add(client);
        }
    }

    public void tryCreateFoursquareClient(final List<Client> clients) {
        final String id = getProperty(FOURSQUARE_ID);
        final String secret = getProperty(FOURSQUARE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final FoursquareClient client = new FoursquareClient(id, secret);
            clients.add(client);
        }
    }

    public void tryCreateGoogleClient(final List<Client> clients) {
        final String id = getProperty(GOOGLE_ID);
        final String secret = getProperty(GOOGLE_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final Google2Client client = new Google2Client(id, secret);
            final String scope = getProperty(GOOGLE_SCOPE);
            if (isNotBlank(scope)) {
                client.setScope(Google2Client.Google2Scope.valueOf(scope.toUpperCase()));
            }
            clients.add(client);
        }
    }

    public void tryCreateYahooClient(final List<Client> clients) {
        final String id = getProperty(YAHOO_ID);
        final String secret = getProperty(YAHOO_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final YahooClient client = new YahooClient(id, secret);
            clients.add(client);
        }
    }

    public void tryCreateDropboxClient(final List<Client> clients) {
        final String id = getProperty(DROPBOX_ID);
        final String secret = getProperty(DROPBOX_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final DropBoxClient client = new DropBoxClient(id, secret);
            clients.add(client);
        }
    }

    public void tryCreateGithubClient(final List<Client> clients) {
        final String id = getProperty(GITHUB_ID);
        final String secret = getProperty(GITHUB_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final GitHubClient client = new GitHubClient(id, secret);
            clients.add(client);
        }
    }

    public void tryCreateTwitterClient(final List<Client> clients) {
        final String id = getProperty(TWITTER_ID);
        final String secret = getProperty(TWITTER_SECRET);
        if (isNotBlank(id) && isNotBlank(secret)) {
            final TwitterClient twitterClient = new TwitterClient(id, secret);
            clients.add(twitterClient);
        }
    }

    public void tryCreateGenericOAuth2Clients(final List<Client> clients) {
        for (int i = 0; i <= MAX_NUM_CLIENTS; i++) {
            final String id = getProperty(OAUTH2_ID, i);
            final String secret = getProperty(OAUTH2_SECRET, i);

            if (isNotBlank(id) && isNotBlank(secret)) {
                final GenericOAuth20Client client = new GenericOAuth20Client();
                client.setName(concat(client.getName(), i));

                client.setKey(id);
                client.setSecret(secret);

                client.setAuthUrl(getProperty(OAUTH2_AUTH_URL, i));
                client.setTokenUrl(getProperty(OAUTH2_TOKEN_URL, i));
                client.setProfileUrl(getProperty(OAUTH2_PROFILE_URL, i));
                client.setProfileNodePath(getProperty(OAUTH2_PROFILE_PATH, i));
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
