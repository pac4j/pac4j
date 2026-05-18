---
layout: ddoc
title: Release process&#58;
---

1) Ensure your SSH key is properly loaded (`ssh-add -l`) and that your security key is loaded as well (GPG)

2) Check that the Javadoc process does not generate any warning: `mvn javadoc:aggregate`

3) Verify that all unit and integration tests pass: `mvn -PforceIT clean verify`

4) Manually run other tests (classes named `RunXXXX`)

5) Release: `mvn release:clean release:prepare` and `mvn release:perform`

6) Publish on the Maven central repository via [https://oss.sonatype.org](https://oss.sonatype.org)
