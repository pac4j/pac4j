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

Many clients are available to login with many identity providers:

- [BitBucket](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/BitbucketClient.java)
- [CAS server using OAuth protocol](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/CasOAuthWrapperClient.java)
- [DropBox](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/DropBoxClient.java)
- [Facebook](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/FacebookClient.java)
- [Foursquare](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/FoursquareClient.java)
- [Github](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/GitHubClient.java)
- [Google](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/Google2Client.java)
- [LinkedIn](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/LinkedIn2Client.java)
- [Odnoklassniki](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/OkClient.java)
- [ORCiD](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/OrcidClient.java)
- [Paypal](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/PayPalClient.java)
- [Strava](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/StravaClient.java)
- [Twitter](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/TwitterClient.java)
- [Vk](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/VkClient.java)
- [Windows Live](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/WindowsLiveClient.java)
- [Word Press](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/WordPressClient.java)
- [Yahoo](https://github.com/pac4j/pac4j/blob/master/pac4j-oauth/src/main/java/org/pac4j/oauth/client/YahooClient.java)

**Example:**

```java
FacebookClient facebookClient = new FacebookClient("145278422258960", "be21409ba8f39b5dae2a7de525484da8");
TwitterClient twitterClient = new TwitterClient("CoxUiYwQOSFDReZYdjigBA", "2kAzunH5Btc4gRSaMr7D7MkyoJ5u1VzbOOzE8rBofs");
```
