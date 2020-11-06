#!/bin/bash

# Only invoke the deployment to Sonatype when it's not a PR and only for main branches
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [[ "$TRAVIS_BRANCH" =~ ^(master|.*\.x)$ ]]; then
  mvn deploy -DskipTests --settings travis/settings.xml
  echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
fi
