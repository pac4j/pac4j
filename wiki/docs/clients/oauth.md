---
layout: doc
title: OAuth
---

*pac4j* allows you to login with identity providers using the OAuth v1.0 and v2.0 protocol.

## 1) Dependency

You need to use the following module: `pac4j-oauth`.

**Example (Maven dependency):**

```xml
<dependency>
    <groupId>org.pac4j</groupId>
    <artifactId>pac4j-oauth</artifactId>
    <version>${pac4j.version}</version>
</dependency>
```

## 2) Available clients

### a) Generic clients

You can use the [`GenericOAuth20Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/GenericOAuth20Client.java) or the [`GenericOAuth20StateClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/GenericOAuth20StateClient.java) clients to login with an OAuth 2.0 server. It returns a [`GenericOAuth20Profile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/generic/GenericOAuth20Profile.java).

**Example:**

```java
GenericOAuth20Client client = new GenericOAuth20Client();
client.setAttributesDefinition(new GitHubAttributesDefinition());
client.setAuthUrl("https://github.com/login/oauth/authorize");
client.setTokenUrl("https://github.com/login/oauth/access_token");
client.setProfileUrl("https://api.github.com/user");
client.setKey("62374f5573a89a8f9900");
client.setSecret("01dd26d60447677ceb7399fb4c744f545bb86359");
client.setCallbackUrl("http://localhost:8080/callback");
client.setScope("user");
```

### b) Specific clients

Many clients are available to login with many identity providers:

| Identity provider | Client | User profile |
|-------------------|--------|---------|
| [BitBucket](https://bitbucket.org) | [`BitbucketClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/BitbucketClient.java) | [`BitbucketProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/bitbucket/BitbucketProfile.java) |
| [CAS server using OAuth protocol](https://apereo.github.io/cas/4.2.x/installation/OAuth-OpenId-Authentication.html) | [`CasOAuthWrapperClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/CasOAuthWrapperClient.java) | [`CasOAuthWrapperProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/casoauthwrapper/CasOAuthWrapperProfile.java) |
| [DropBox](https://www.dropbox.com) | [`DropBoxClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/DropBoxClient.java) | [`DropBoxProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/dropbox/DropBoxProfile.java) |
| [Facebook](https://www.facebook.com/) | [`FacebookClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/FacebookClient.java) | [`FacebookProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/facebook/FacebookProfile.java) |
| [Foursquare](https://www.foursquare.com) | [`FoursquareClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/FoursquareClient.java) | [`FoursquareProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/foursquare/FoursquareProfile.java) |
| [Github](https://github.com) | [`GitHubClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/GitHubClient.java) | [`GitHubProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/github/GitHubProfile.java) |
| [Google](https://www.google.com) | [`Google2Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/Google2Client.java) | [`Google2Profile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/google2/Google2Profile.java) |
| [LinkedIn](https://www.linkedin.com/) | [`LinkedIn2Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/LinkedIn2Client.java) | [`LinkedIn2Profile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/linkedin2/LinkedIn2Profile.java) |
| [Odnoklassniki](https://ok.ru/) | [`OkClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/OkClient.java) | [`OkProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/ok/OkProfile.java) |
| [ORCiD](http://orcid.org/) | [`OrcidClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/OrcidClient.java) | [`OrcidProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/orcid/OrcidProfile.java) |
| [Paypal](https://www.paypal.com) | [`PayPalClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/PayPalClient.java) | [`PayPalProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/paypal/PayPalProfile.java) |
| [Strava](https://www.strava.com/) | [`StravaClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/StravaClient.java) | [`StravaProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/strava/StravaProfile.java) |
| [Twitter](https://twitter.com/) | [`TwitterClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/TwitterClient.java) | [`TwitterProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/twitter/TwitterProfile.java) |
| [Vk](https://vk.com/) | [`VkClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/VkClient.java) | [`VkProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/vk/VkProfile.java) |
| [Windows Live](https://login.live.com/) | [`WindowsLiveClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/WindowsLiveClient.java) | [`WindowsLiveProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/windowslive/WindowsLiveProfile.java) |
| [Word Press](https://wordpress.com) | [`WordPressClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/WordPressClient.java) | [`WordPressProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/wordpress/WordPressProfile.java) |
| [Yahoo](https://www.yahoo.com) | [`YahooClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/YahooClient.java) | [`YahooProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/profile/yahoo/YahooProfile.java) |
{:.table-striped}

**Example:**

```java
FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
```
