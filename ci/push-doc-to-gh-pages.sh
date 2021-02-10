#!/bin/bash

echo -e "Starting with project documentation...\n"

echo -e "Copying project documentation over to $HOME/docs-latest...\n"
cp -R documentation $HOME/docs-latest

echo -e "Finished with project documentation...\n"

cd $HOME
git config --global user.email "actions@github.com"
git config --global user.name "Github Actions"
echo -e "Cloning the gh-pages branch...\n"
git clone --depth 1 --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/pac4j/pac4j gh-pages > /dev/null

cd gh-pages

echo -e "Starting to move project documentation over...\n"

export BRANCH=${GITHUB_REF#"refs/heads/"}
echo -e "BRANCH: $BRANCH"

if [ "$GITHUB_REF" == "refs/heads/master" ]; then

    echo -e "Copying new docs from $HOME/docs-latest over to gh-pages...\n"
    cp -Rf $HOME/docs-latest/* .
    echo -e "Copied project documentation...\n"

else

    echo -e "Removing previous documentation from $BRANCH...\n"
    git rm -rf ./"$BRANCH" > /dev/null

    echo -e "Creating $BRANCH directory...\n"
    test -d "./$BRANCH" || mkdir -m777 -v "./$BRANCH"

    echo -e "Copying new docs from $HOME/docs-latest over to $BRANCH...\n"
    cp -Rf $HOME/docs-latest/* "./$BRANCH"
    echo -e "Copied project documentation...\n"

fi

echo -e "Adding changes to the git index...\n"
git add -f . > /dev/null

echo -e "Committing changes...\n"
git commit -m "Published doc from $BRANCH to [gh-pages]. Build ${GITHUB_RUN_ID}-${GITHUB_RUN_NUMBER} " > /dev/null

echo -e "Pushing upstream to origin...\n"
git push -fq origin gh-pages > /dev/null

echo -e "Successfully published documentation to [gh-pages] branch.\n"

echo $?
