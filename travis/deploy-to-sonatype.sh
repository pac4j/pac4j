#!/bin/bash

# Only invoke the deployment to Sonatype when it's not a PR and not gh-pages
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" != "gh-pages" ]; then
  mvn deploy -DskipTests --settings travis/settings.xml
  echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
fi
