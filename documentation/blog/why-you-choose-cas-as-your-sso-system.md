---
layout: blog
title: Why you should choose CAS as your SSO system
author: Jérôme LELEU
date: October 2018
---

Most of the customers for whom I work have already chosen the CAS server and its ecosystem when I come in to help them.

Yet, from time to time, I'm contacted earlier in the decision process and the client is still thinking about the products and protocols he wants to use. Advocating for the CAS server is an easy task for me, even if generally, technical reasons are just a small part of the game (politics and relationships really matter).

Some may think that as a CAS committer and as an IAM freelance, I'm biased towards CAS. This is true, but certainly less than any salesman who will earn fees for each sold product.

Eventually, this is what I say:

### 1) CAS is a very modern software

While the general code quality of the CAS server has always been good, I must admit that the whole architecture and design have strongly improved since version 5 where it has embraced the Spring Boot ecosystem. I was a bit reluctant on this huge change, but Misagh was definitely right as using Spring Boot was a big jump forward.
There are some glitches here and there, but overall, the code is easy to read and understand and things are really modular.


### 2) CAS has many features

The CAS server offers many useful capabilities, from obvious ones to more advanced ones. It handles various authentication methods, multi-factor authentication (MFA), can remember the user, force or try authentication, offers a dashboard and monitoring, has logs and audits, manages passwords, etc.
Of course, you cannot only assess a software by the number of its features but having many options is generally a good thing.


### 3) CAS is very popular

The CAS server is one of the most popular software in IAM. With around five thousands stars on github.com, CAS is a well-known project.
Many higher-education schools and companies use it, so you can easily find help from the CAS community or paid consultants. The more the CAS server is installed, the more people read its source code and test it, the more security vulnerabilities are discovered and fixed. It makes it a safe choice for your SSO system.


### 4) CAS fits everywhere

Your existing environment is the first most important criteria when choosing an SSO system. Most companies never start a new project from scratch: for example, they use Redis on CentOS, a bit of MongoDB also and their users' storage is an old LDAP system. You must cope with all that constraints.
If your software only supports technologies A and B, you can only address clients willing to use A or B. The CAS server can:
- use many authentication methods: LDAP, databases, X509, Cassandra, Radius, Spnego, JWT, AWS Cloud Directory and many more
- handle many storage systems: Memcached, Redis, Oracle, Hazelcast, CouchDB, Couchbase, etc.


### 5) CAS is the master of interoperability

Your existing environment is also the second most important criteria when choosing an SSO system! Big bang projects almost always fail and it's hard to deal with legacy. I don't remember having a customer without an existing software to integrate with.
And this is the great strength of CAS:
- the CAS server can act as a SAML IdP, an OAuth provider, an OpenID Connect provder or as a CAS server of course
- but it can also play the role of a client delegating the authentication to another CAS server, to a SAML IdP, to Facebook, Google, Twitter and many other identity providers.

This is one of the main contributions I brought to CAS and I'm especially proud of this interoperability.

CAS clients used for integration in web applications exist in many technologies as well: Java, PHP, .Net, Ruby, Python, Node.js, Apache module, etc.



CAS is a great software, it is worth considering if you plan to choose an SSO system.
