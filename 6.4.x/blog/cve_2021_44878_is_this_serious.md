---
layout: blog
title: CVE-2021-44878&colon; is this serious?
author: Jérôme LELEU
date: March 2022
---

A few weeks ago, a security vulnerability was published about pac4j: [CVE-2021-44878](https://nvd.nist.gov/vuln/detail/CVE-2021-44878).

I have received many questions and requests related to this security vulnerability and I'd like to publicly reply to them.

Here si the description of the CVE:

*Pac4j v5.1 and earlier allows (by default) clients to accept and successfully validate ID Tokens with "none" algorithm (i.e., tokens with no signature) which is not secure and violates the OpenID Core Specification. The "none" algorithm does not require any signature verification when validating the ID tokens, which allows the attacker to bypass the token validation by injecting a malformed ID token using "none" as the value of "alg" key in the header with an empty signature value.*

and its CVSS score is: **7.5 HIGH**

It seems pretty serious, isn't it?

Not really.

First of all, I disagree with this description as it lets people think that there is a problem in pac4j alone, that it solely accepts unsigned tokens.

This is not the case: pac4j only instantiates the validators related to the supported algorithms of the OIDC server (published in its metadata).

But yes, pac4j should do something about it to follow the OIDC specification which says:

*ID Tokens MUST NOT use none as the alg value unless the Response Type used returns no ID Token from the Authorization Endpoint (such as when using the Authorization Code Flow) and the Client explicitly requested the use of none at Registration time.*

pac4j should request explicit approval to support the "none" algorithm and refuse to deal with it if the "idtoken" is requested in the response type.

The first part was fixed in pac4j v5.1.0 (you can use: `oidcConfig.setAllowUnsignedIdTokens(true)`) and the second part was fixed in pac4j v5.3.1.

So the right description should be:

*If an OpenID Connect provider supports the "none" algorithm (i.e., tokens with no signature), pac4j v5.3.0 (and prior) does not refuse it without an explicit configuration on its side or for the "idtoken" response type which is not secure and violates the OpenID Core Specification. The "none" algorithm does not require any signature verification when validating the ID tokens, which allows the attacker to bypass the token validation by injecting a malformed ID token using "none" as the value of "alg" key in the header with an empty signature value.*

Finally, this security vulnerability only exists if the "none" algorithm is used by the OIDC server, even though pac4j should have provided better protection against it.

Does it make sense to use the "none" algorithm in production for an OIDC server?

Does it make sense not to sign the ID tokens provided after a successful OIDC authentication?

I would very much doubt it.

Please don't hesitate to report me any OIDC server supporting the "none" algorithm for its ID tokens.

Does a security vulnerability that is unlikely and mainly related to another component misconfiguration can be considered serious? I don't think so.

@CVE/NVD teams, please change the CVE description and the score.
