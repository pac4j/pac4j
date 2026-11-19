---
layout: blog
title: Sovereign Tech Agency funding for pac4j v7
author: Jérôme LELEU
date: November 23, 2026
draft: true
tags: [fund]
seo_title: "Sovereign Tech Agency funding for pac4j v7, OpenID Federation and OpenID4VP | pac4j"
description: "pac4j is funded by the Sovereign Tech Agency: pac4j v7 on JDK 21, a more complete OpenID Federation and OpenID4VP support, and the OpenID certifications."
---

I am really happy to announce that, a few days ago, pac4j was awarded funding by the <a href="https://www.sovereign.tech" target="_blank">Sovereign Tech Agency</a>. 🍾 🎉 After <a href="/blog/nlnet_eudiwallet.html">NLnet</a> in August, this is the second time pac4j is sponsored by a European organisation.

Beyond the money, and therefore the time it gives me to work on the project, I take it as a real recognition of the usefulness of the library and of the work accomplished over the past 15 years.


## Where pac4j stands

The latest releases have brought two new protocols to pac4j:

- [OpenID Federation](/docs/clients/openid-connect-federation.html) in v6.4.0
- [OpenID4VP](/docs/clients/openid4vp.html), for the EUDI Wallet / eIDAS 2.0, in v6.6.0.

This funding makes it possible to consolidate all this work, while opening a new stream: **pac4j v7**, based on JDK 21.


## What is funded

Four tasks are sponsored.

### 1) New version 7 of pac4j

Started in December 2026:

- upgrade to JDK 21
- removal of the deprecated components and supports
- CAS support based on the CAS client v4.1
- code review of all modules
- move to plain SQL for the `pac4j-sql` module.

Then, in 2027: upgrade of the 15 [pac4j implementations](/implementations.html) currently available for pac4j v6, and maybe more.

### 2) Going further with OpenID Federation

Since v6.4.0, a pac4j application can join a federation as a relying party: it publishes its entity configuration and resolves the OpenID Provider through trust anchors, as tested with Connect2id and CAS. This task completes the implementation of the specification on the relying party side:

- bug fixes and improvements: `jwks_uri`, `signed_jwks_uri`, trust marks...
- sending the resolved trust chain upfront
- extraction of the support into its own module (`pac4j-openidfederation`), to be reused by OpenID4VP.

### 3) Going further with OpenID4VP

Since v6.6.0, a pac4j application can authenticate users with a digital wallet, with SD-JWT VC and mdoc credentials, on the same device, on another device or through the Digital Credentials API. This task completes the implementation of the specification with what is still missing:

- `verifier_attestation` and `openid_federation` client identifier prefixes
- `transaction_data` for the SD-JWT VC and mdoc verifiers
- more testing on various flows and wallet playgrounds.

### 4) Official OpenID certifications

The goal is to get pac4j officially certified by the <a href="https://openid.net/certification/" target="_blank">OpenID Foundation</a> for OpenID Connect, OpenID Federation and OpenID4VP: passing the conformance tests, and then the official certifications themselves, whose fees are covered by the funding.


## Timeline

pac4j v7.0 is planned for the end of January 2027. The whole work, ending with the certifications, should be completed by mid-2027.


## How you can help

The work is done in the open, in the <a href="https://github.com/pac4j/pac4j" target="_blank">pac4j repository</a> (as always, plain Open Source). If you have needs or feedback on pac4j v7, OpenID Federation or OpenID4VP, I'd be glad to discuss them on the <a href="https://groups.google.com/forum/?fromgroups#!forum/pac4j-dev" target="_blank">pac4j-dev mailing list</a>.
