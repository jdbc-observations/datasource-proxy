#!/bin/bash
#
#  Publish snapshot version document
#

set -e # exit with nonzero exit code if anything fails

echo "Branch=\"${TRAVIS_BRANCH}\" Tag=\"${TRAVIS_TAG}\" PR=\"${TRAVIS_PULL_REQUEST}\""

if [ "$TRAVIS_BRANCH" != 'master' ] || [ "$TRAVIS_PULL_REQUEST" == 'true' ]; then
    echo "Not publishing snapshot version documents."
    exit 0
fi

./mvnw -Ppublish-doc asciidoctor:process-asciidoc@output-html javadoc:javadoc

cd $HOME
rm -fr gh-pages

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ttddyy/datasource-proxy gh-pages > /dev/null

cd gh-pages
mkdir -p docs/snapshot/user-guide/
mkdir -p docs/snapshot/api/
cp -Rf $TRAVIS_BUILD_DIR/target/generated-docs/index.html docs/snapshot/user-guide/
cp -Rf $TRAVIS_BUILD_DIR/target/site/apidocs/* docs/snapshot/api/
git add -f .
git commit -m "Latest documentation on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null
