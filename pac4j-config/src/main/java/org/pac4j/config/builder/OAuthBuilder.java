package org.pac4j.config.builder;

import org.pac4j.core.client.Client;
import org.pac4j.oauth.client.*;

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

    public static final String FACEBOOK_ID = "facebook.id";
    public static final String FACEBOOK_SECRET = "facebook.secret";
    public static final String FACEBOOK_SCOPE = "facebook.scope";
    public static final String FACEBOOK_FIELDS = "facebook.fields";

    public static final String TWITTER_ID = "twitter.id";
    public static final String TWITTER_SECRET = "twitter.secret";

    public static final String GITHUB_ID = "github.id";
    public static final String GITHUB_SECRET = "github.secret";

    public static final String DROPBOX_ID = "dropbox.id";
    public static final String DROPBOX_SECRET = "dropbox.secret";

    public static final String WINDOWSLIVE_ID = "windowslive.id";
    public static final String WINDOWSLIVE_SECRET = "windowslive.secret";

    public static final String YAHOO_ID = "yahoo.id";
    public static final String YAHOO_SECRET = "yahoo.secret";

    public static final String LINKEDIN_ID = "linkedin.id";
    public static final String LINKEDIN_SECRET = "linkedin.secret";
    public static final String LINKEDIN_FIELDS = "linkedin.fields";
    public static final String LINKEDIN_SCOPE = "linkedin.scope";

    public static final String FOURSQUARE_ID = "foursquare.id";
    public static final String FOURSQUARE_SECRET = "foursquare.secret";

    public static final String GOOGLE_ID = "google.id";
    public static final String GOOGLE_SECRET = "google.secret";
    public static final String GOOGLE_SCOPE = "google.scope";

    public OAuthBuilder(final Map<String, String> properties) {
        super(properties);
    }

    public void tryCreateLinkedInClient(final List<Client> clients) {
        final String id = getProperty(LINKEDIN_ID);
        final String secret = getProperty(LINKEDIN_SECRET);
        final String scope = getProperty(LINKEDIN_SCOPE);
        final String fields = getProperty(LINKEDIN_FIELDS);

        if (isNotBlank(id) && isNotBlank(secret)) {
            final LinkedIn2Client linkedInClient = new LinkedIn2Client(id, secret);
            if (isNotBlank(scope)) {
                linkedInClient.setScope(scope);
            }
            if (isNotBlank(fields)) {
                linkedInClient.setFields(fields);
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
}
