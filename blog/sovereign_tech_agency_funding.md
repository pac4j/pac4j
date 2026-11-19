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

### 1) pac4j v7

Started in December 2026:

- upgrade to JDK 21
- removal of the deprecated components and supports
- CAS support based on the CAS client v4.1
- code review of all modules
- move to plain SQL for the `pac4j-sql` module.

Then, in 2027: upgrade of the 15 [pac4j implementations](/implementations.html) currently available for pac4j v6, and maybe more.

### 2) OpenID Federation

- bug fixes and improvements: `jwks_uri`, `signed_jwks_uri`, trust marks...
- sending the resolved trust chain upfront
- extraction of the support into its own module (`pac4j-openidfederation`), to be reused by OpenID4VP.

### 3) OpenID4VP

- `verifier_attestation` and `openid_federation` client identifier prefixes
- `transaction_data` for the SD-JWT VC and mdoc verifiers
- more testing on various flows and wallet playgrounds.

### 4) OpenID certifications

The goal is to make pac4j pass the official OpenID certification tests for OpenID Connect, OpenID Federation and OpenID4VP.


## How you can help

The work is done in the open, in the <a href="https://github.com/pac4j/pac4j" target="_blank">pac4j repository</a> (as always, plain Open Source). If you have needs or feedback on pac4j v7, OpenID Federation or OpenID4VP, I'd be glad to discuss them on the <a href="https://groups.google.com/forum/?fromgroups#!forum/pac4j-dev" target="_blank">pac4j-dev mailing list</a>.
