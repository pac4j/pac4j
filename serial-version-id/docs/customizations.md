---
layout: doc
title: Customizations&#58;
---

*pac4j* comes with a huge set of components for various needs, so before any customization, you should carefully read the [Clients](clients.html), [Authenticators](authenticators.html) and [Authorizers](authorizers.html) pages to check what is already provided.


### Customizing the authentication/authorization components:

Be sure to clearly understand what the roles of the different components are:

- a [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) is a whole login process: it is indirect for UI ([`IndirectClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/IndirectClient.java)) and direct for web services ([`DirectClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/DirectClient.java). It redirects to the identity provider (indirect client only), extracts the user credentials, validates the user credentials and creates a user profile for the authenticated user
- a [`RedirectionActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectionActionBuilder.java) redirects the user to the identity provider for login (indirect clients)
- a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) extracts the user credentials from the HTTP request (indirect and direct clients)
- an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) validates the user credentials (indirect and direct clients)
- a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) creates a user profile for the authenticated user (indirect and direct clients)
- an [`Authorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/Authorizer.java) allows access based on the user profiles or on the web context
- a [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) defines if the security must apply on the web context
- an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) generates the appropriate roles and permissions for a given user profile.

Overriding or creating new components should be straightforward.

Nonetheless, building a `Client` requires extra efforts. Notice that:

- you really need to understand what kind of authentication mechanism you want to support: is it for UI (credentials are provided only once and authentication almost always occurs at an external identity provider) or for web services (credentials are passed for every request)

- all clients should implement the `IndirectClient` interface and define the appropriate `RedirectionActionBuilder`, `CredentialsExtractor`, `Authenticator` and `ProfileCreator` (and optional `LogoutActionBuilder`)

- it may require to create a new [`Credentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java) type (if it is not a simple string designed by the `TokenCredentials` or a username/password designed by the `UsernamePasswordCredentials`). These new credentials may inherit from the base credentials of the supported protocol (like `OAuthCredentials`)

- it is generally a good practice to create a new profile for a new client (whether this profile will have or not specific data) to be able to distinguish between all user profiles.
The new user profile should certainly inherit from the base profile of the protocol support, like `OAuth20Profile`.
At least, it must inherit from [`CommonProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/CommonProfile.java).
The data returned by the identity provider may need to be converted (a single string into a Java enumeration for example) and for that, converters (classes extending [`AttributeConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/AttributeConverter.java)) are necessary. Both the converters and the returned user profile class must be defined in a [`ProfileDefinition`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/definition/ProfileDefinition.java).


### Changing the core flow:

Overriding or creating new components allows you to implement new behaviour inside the boundaries of the defined [logics](how-to-implement-pac4j-for-a-new-framework.html) of the regular pac4j "filters".
Though, in some cases, it may not be enough. So you may decide to break the flow and change the provided behaviour by requesting some extra actions.
And this can be done by throwing an [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/HttpAction.java) (like any exception) as most components allow that.

**Example:**

```java
public class ExampleAuthorizer implements Authorizer<CommonProfile> {

    @Override
    public boolean isAuthorized(WebContext context, List<CommonProfile> profiles) throws HttpAction {
        if ("specificValue".equals(context.getRequestHeader("specificHeader")))
        {
            throw HttpAction.redirect("force redirection", context, "/message.html");
        }
        return true;
    }
}
```

### Customizing the web integration

*pac4j* implementations heavily rely on the `WebContext` and `SessionStore` to deal with the HTTP request, response and session. The default implementations of theses component may be override or replaced.

As well as the default `ProfileManager` (used to save/restore the profile) or `GuavaStore` (to save data in cache).


In all cases, there is nothing better than taking a look at the existing components as examples. Don't hesitate to ask any question on the [pac4j-dev mailing list](https://groups.google.com/forum/?fromgroups#!forum/pac4j-dev).
