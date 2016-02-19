#!/bin/bash -x
set -e # exit with nonzero exit code if anything fails

cd $HOME
rm -fr gh-pages

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ttddyy/datasource-proxy gh-pages > /dev/null

cd gh-pages
rm -fr docs/datasource-proxy
rm -fr docs/datasource-test-proxy
cp -Rf $TRAVIS_BUILD_DIR/datasource-proxy/target/generated-docs docs/datasource-proxy
cp -Rf $TRAVIS_BUILD_DIR/datasource-test-proxy/target/generated-docs docs/datasource-test-proxy
git add -f .
git commit -m "Latest documentation on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
git push -fq origin gh-pages > /dev/null
