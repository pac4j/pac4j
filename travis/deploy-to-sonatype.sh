#!/bin/bash

echo -e "TRAVIS_PULL_REQUEST: $TRAVIS_PULL_REQUEST"
echo -e "TRAVIS_BRANCH: $TRAVIS_BRANCH"

# Only invoke the deployment to Sonatype when it's not a PR and only for main branches
if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [[ "$TRAVIS_BRANCH" =~ ^(master|.*0\.x)$ ]]; then
  echo -e "Deploying SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
  mvn deploy -DskipTests --settings travis/settings.xml
  echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
fi
