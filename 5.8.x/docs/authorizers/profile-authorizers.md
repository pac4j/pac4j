---
layout: doc
title: User profile authorizers&#58;
---

Some authorizers only apply on the user profile:

## 1) Roles/permissions

To check roles and permissions on the user profile, you first need to compute them with an [`AuthorizationGenerator`](../clients.html#compute-roles-and-permissions).

- [`RequireAnyRoleAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAnyRoleAuthorizer.java) checks that a user profile has at least one of the expected roles or at least one role if none is defined

- [`RequireAllRolesAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAllRolesAuthorizer.java) checks that a user profile has all the expected roles

- [`RequireAnyPermissionAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAnyPermissionAuthorizer.java) checks that a user profile has at least one of the expected permissions or at least one permission if none is defined

- [`RequireAllPermissionsAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAllPermissionsAuthorizer.java) checks that a user profile has all the expected permissions


## 2) Authentication levels

- [`IsAnonymousAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsAnonymousAuthorizer.java) checks that the user is anonymous (no profile or an `AnonymousProfile`)

- [`IsAuthenticatedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsAuthenticatedAuthorizer.java) checks that the user has the profile which is not an `AnonymousProfile`

- [`IsFullyAuthenticatedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsFullyAuthenticatedAuthorizer.java) checks that the user is authenticated, but not remembered (`isRemembered` method)

- [`IsRememberedAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/IsRememberedAuthorizer.java) checks that the user is authenticated and only remembered (`isRemembered` method)


## 3) Others

- [`CheckProfileTypeAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/CheckProfileTypeAuthorizer.java) checks the current profile type of the authenticated user

- [`RequireAnyAttributeAuthorizer`](https://github.com/pac4j/pac4j/blob/master/pac4j-core/src/main/java/org/pac4j/core/authorization/authorizer/RequireAnyAttributeAuthorizer.java) checks that the current profile has the appropriate attribute
