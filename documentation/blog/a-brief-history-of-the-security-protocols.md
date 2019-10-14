---
layout: blog
title: A brief history of the security protocols
author: Jérôme LELEU
date: September 2019
---

This is certainly the number one presentation I make when doing consulting missions for clients. While most people I meet have some knowledge about the security protocols, they generally lack the historical background, the broader vision and the hindsight that would help them make the best choices. So let’s talk about the security protocols and let’s try to make it a little funny, right?

At first, there was <strong>SAML</strong>. This is a standard of the OASIS consortium, which has produced several standards around the XML technology.  
There was a version 1.0 in 2002 and then a version 2.0 in 2005. I don't hear that much about version 1.0, but version 2.0 is incredibly widespread, not to say popular. In most companies, there is always a SAML IdP (a SAML server) somewhere to be integrated with.  
SAML is mainly a web authentication protocol based on XML requests/responses which makes it fairly verbose. It is rather complicated and I'm often contacted by people to help them with their SAML integration.  
Behind OASIS, you have IBM, Microsoft, Oracle, ... and the SAML standard is at their images: heavy!

At the same time or maybe even earlier, there is <strong>CAS</strong>. It was born at Yale University and therefore widely used in the higher education system.  
It's a standard somehow, but it lacks a consortium or authority behind it. This is why properly returning user attributes has taken too much time to be standardized for example. It's a protocol but it's also a  software at the same time (the CAS server in Java).  
CAS is a web authentication protocol but it targets UIs as well as web services, which was very innovative in the early 2000s. The web services support is unfortunately underused. I still believe in it yet.  
Although it's also based on XML, it's at the other end of the Spectrum compared to SAML. It's very easy with limited configuration options (the CAS server login URL is mostly what you need). It's a protocol made by people who just wanted to make it work, no licenses or software to sell.

<strong>OpenID</strong> comes after that, in 2007. OpenID is not OpenID Connect (like Java is not Javascript). It's under the umbrella of the OpenID foundation. It's the rise of the web and its members are Yahoo, Google, Facebook...  
It's a web authentication protocol made by web pure players, not software vendors. It was built with one smart idea: make OpenID identity providers discovery easy based on the OpenID identifier. Putting this idea in practice was fairly complicated and it has never reached the expected success.  
It's a protocol I have forgotten a long time ago and for which I don't receive any business request, nor by clients or in the Open Source space.  
In one word: dead.

At the same time, <strong>OAuth</strong> is born with version 1.0 (2007) and a few years later a version 2.0 (2012) has come to increase its adoption. The story says that a guy from Twitter didn't want to use the OpenID protocol from Yahoo and invent its own security protocol.  
The version 1.0 was rather complicated with a token exchange and I almost don't hear about it anymore. Version 2.0 is much easier but the original spec was incomplete enough to leave space for custom developments and vulnerabilities. It's much better now.  
As opposed to the previous protocols, OAuth is an authorization protocol. Of course, before getting any authorization, the user must be authenticated which leads to some confusion on the matter. In the end though, the application does not know the user but gets a token to access the user's data. Generally, the application can know the user by getting his profile, but this could not be the case.  
This authorization nature of the OAuth protocol is not surprising given the rise of the web services these last years and the companies behind the OAuth RFCs: Google, Twitter, Facebook... Their business model is mostly to expose your data ;-)

<strong>OpenID Connect</strong> is born in 2014, it's a very new protocol built on top of (the success of) the OAuth protocol v2. It's the come back of the OpenID foundation which is behind this new standard.
Taken from their official website: "OpenID Connect performs many of the same tasks as OpenID 2.0, but does so in a way that is API-friendly, and usable by native and mobile applications."  
To make things clearer, OpenID Connect is an authentication layer on top of OAuth, thus it does both: authentication and authorization. Related to authentication, some specs deal with the logout mechanism and the protocol uses the JWT technology to return an ID token containing the user identity (in addition to the access token).  
One more level of complexity...

As we know, the world has changed: the early websites have been replaced by a galaxy of web services and the powerful leaders of yesterday have been overwhelmed by the new web players. The security protocols follow history.
