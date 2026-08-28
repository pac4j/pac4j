# Security policy

## Reporting a vulnerability

**Please do not report security issues through public GitHub issues, pull requests or discussions.**

Report it privately through GitHub: **[Report a vulnerability](https://github.com/pac4j/pac4j/security/advisories/new)**.
If you prefer email, send it to **security@pac4j.org**.

Please include as much as you can:

- the affected module (`pac4j-core`, `pac4j-oidc`, `pac4j-saml`...) and version
- the protocol or component involved
- a description of the issue and its impact
- the steps or a minimal project to reproduce it

You will receive an acknowledgement of your report **within one week at the most**. We will then
confirm the issue, prepare a fix and coordinate the release with you.

## Supported versions

| Line     | Status                                          | Minimum JDK |
|----------|-------------------------------------------------|-------------|
| **6.x**  | Actively maintained                             | 17          |
| **5.x**  | Not maintained — critical fixes backported      | 11          |
| **4.x**  | Not maintained — critical fixes backported      | 8           |
| 3.x and earlier | End of life — do not use                 | —           |

Only the **6.x** line receives bug fixes, improvements and regular security updates.

The 4.x and 5.x lines are no longer maintained: they get no bug fix and no new feature. A fix is
backported to them only when the vulnerability is **critical** — that decision is made case by case
when the issue is triaged. If you are still on one of these lines, plan your upgrade to 6.x rather
than rely on a backport.

If you are on an end-of-life line, upgrade before reporting: we will not issue fixes for it.

## Disclosure

Once a fix is released:

- an advisory is published on the [pac4j blog](https://www.pac4j.org/blog.html), naming the fixed
  versions to upgrade to;
- the advisory deliberately contains **no technical details**, so that users have time to upgrade;
- the reporter is credited, unless they ask otherwise.

## Staying informed

- Subscribe to the [pac4j-security](https://groups.google.com/forum/?fromgroups#!forum/pac4j-security)
  Google group to receive security alerts.
- Subscribe to the [pac4j-announce](https://groups.google.com/forum/?fromgroups#!forum/pac4j-announce)
  Google group to receive release announcements.

The practical takeaway: **use the latest mature version of pac4j and apply security updates as soon
as possible.**
