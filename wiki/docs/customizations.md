---
layout: ddoc
title: Customizations&#58;
---

pac4j comes with a huge set of components for various needs, so before any customization, you should carefully read the [Clients](/docs/clients.html), [Authenticators](/docs/authenticators.html) and [Authorizers](/docs/authorizers.html) pages to check what is already provided.

### Customizing components:

Be sure to clearly understand what the roles of the different components are:

- a [`Client`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/Client.java) is a whole login process: it is indirect for UI ([`IndirectClientV2`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/IndirectClientV2.java)) and direct for web services ([`DirectClientV2`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/DirectClientV2.java). It redirects to the identity provider (indirect client only), extracts the user credentials, validates the user credentials and creates a user profile for the authenticated user
- a [`RedirectActionBuilder`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/redirect/RedirectActionBuilder.java) redirects the user to the identity provider for login (indirect clients)
- a [`CredentialsExtractor`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/extractor/CredentialsExtractor.java) extracts the user credentials from the HTTP request (indirect and direct clients)
- an [`Authenticator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/authenticator/Authenticator.java) validates the user credentials (indirect and direct clients)
- a [`ProfileCreator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/creator/ProfileCreator.java) creates a user profile for the authenticated user (indirect and direct clients)
- an [`Authorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/Authorizer.java) allows access based on the user profiles or on the web context
- a [`Matcher`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/matching/Matcher.java) defines if the security must apply on the web context
- an [`AuthorizationGenerator`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/generator/AuthorizationGenerator.java) generates the appropriate roles and permissions for a given user profile.

Overriding or creating new components should be straightforward.

Nonetheless, building a `Client` requires extra efforts. Notice that:

- you really need to understand what kind of authentication mechanism you want to support: is it for UI (credentials are provided only once and authentication almost always occurs at an external identity provider) or for web services (credentials are passed for every request)

- some indirect clients implement the deprecated [`IndirectClient`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/client/IndirectClient.java) interface when the redirection, authentication process and profile retrieval are closely linked to the authentication mechanism. All should implement the `IndirectClientV2` interface and define the appropriate `RedirectActionBuilder`, `CredentialsExtractor`, `Authenticator` and `ProfileCreator`

- it may require to create a new [`Credentials`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/credentials/Credentials.java) type (if it is not a simple string designed by the `TokenCredentials` or a username / password designed by the `UsernamePasswordCredentials`). These new credentials may inherit from the base credentials of the supported protocol (like `OAuthCredentials`)

- it is generally a good practice to create a new profile for a new client (whether this profile will have or not specific data) to be able to distinguish between all user profiles. The new user profile should certainly inherit from the base profile of the protocol support, like `OAuth20Profile`. At least, it must inherit from [`CommonProfile`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/CommonProfile.java). The data returned by the identity provider may need to be converted (a single string into a Java enumeration for example) and for that, converters (classes extending [`AttributeConverter`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/converter/AttributeConverter.java)) and an [`AttributesDefinition`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/profile/AttributesDefinition.java) are necessary.

### Customizing the behaviour:

Overriding or creating new components allows you to implement new behaviour inside the boundaries of the defined [logics](/docs/how-to-implement-pac4j-for-a-new-framework.html) of the regular pac4j "filters". Though, in some cases, it may not be enough. So you may decide to break the flow and change the provided behaviour by requesting some extra actions. And this can be done by throwing an [`HttpAction`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/exception/HttpAction.java) (like any exception) as most components allow that.

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

In all cases, there is nothing better than taking a look at the existing components as examples. Don't hesitate to ask any questions on the [pac4j-dev mailing list](https://groups.google.com/forum/?fromgroups#!forum/pac4j-dev).
