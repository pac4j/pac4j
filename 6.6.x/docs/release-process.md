---
layout: ddoc
title: Release process&#58;
seo_title: "Maintainer guide to publishing a release | pac4j"
description: "Follow the pac4j release process, from Javadoc checks and Maven release commands to publishing artifacts on Maven Central."
---

1) Ensure your SSH key is properly loaded (`ssh-add -l`) and that your security key is loaded as well (GPG)

2) Check that the Javadoc process does not generate any warning: `mvn javadoc:aggregate`

3) Release: `mvn release:clean release:prepare`

4) Release: `mvn release:perform`

5) Publish on the Maven central repository via [https://central.sonatype.com/](https://central.sonatype.com/)
