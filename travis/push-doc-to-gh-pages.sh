#!/bin/bash
invokeDoc=false
branchVersion="development"

if [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
  case "${TRAVIS_JOB_NUMBER}" in
       *\.1)
        echo -e "Invoking auto-doc deployment for Travis job ${TRAVIS_JOB_NUMBER}"
        invokeDoc=true;;
  esac
fi

echo -e "Starting with project documentation...\n"

if [ "$invokeDoc" == true ]; then

  echo -e "Copying project documentation over to $HOME/docs-latest...\n"
  cp -R documentation $HOME/docs-latest

  echo -e "Finished with project documentation...\n"

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  echo -e "Cloning the gh-pages branch...\n"
  git clone --depth 1 --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/pac4j/pac4j gh-pages > /dev/null

  cd gh-pages

  echo -e "Staring to move project documentation over...\n"

  echo -e "Copying new docs from $HOME/docs-latest over to gh-pages...\n"
  cp -Rf $HOME/docs-latest/* .
  echo -e "Copied project documentation...\n"

  echo -e "Adding changes to the git index...\n"
  git add -f . > /dev/null

  echo -e "Committing changes...\n"
  git commit -m "Published documentation from $TRAVIS_BRANCH to [gh-pages]. Build $TRAVIS_BUILD_NUMBER " > /dev/null

  echo -e "Pushing upstream to origin...\n"
  git push -fq origin gh-pages > /dev/null

  echo -e "Successfully published documentation to [gh-pages] branch.\n"

fi
