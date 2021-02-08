#!/bin/bash

echo -e "GITHUB_HEAD_REF: ${GITHUB_HEAD_REF}"
echo -e "GITHUB_REF: ${GITHUB_REF}"

# Only invoke the deployment to Sonatype when it's not a PR and only for main branches
if [ "$GITHUB_HEAD_REF" == "" ] && [[ "$GITHUB_REF" =~ ^refs/heads/(master|.*\.x)$ ]]; then
  echo -e "Deploying snapshot artifacts to Sonatype under job ${GITHUB_RUN_ID}"
  mvn deploy -DskipTests
  echo -e "Successfully deployed snapshot artifacts to Sonatype under job ${GITHUB_RUN_ID}"
fi
