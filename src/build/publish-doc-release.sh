#!/bin/bash
#
#  Publish release version document
#
set -e # exit with nonzero exit code if anything fails

echo "Branch=\"${TRAVIS_BRANCH}\" Tag=\"${TRAVIS_TAG}\" PR=\"${TRAVIS_PULL_REQUEST}\""

if [ "$TRAVIS_BRANCH" != "$TRAVIS_TAG" ]; then
    echo "Not publishing release version documents."
    exit 0
fi

# tag example: datasource-proxy-1.0
RELEASE_VERSION=`echo ${TRAVIS_TAG} | sed 's/.*-//'`
echo "Release version=${RELEASE_VERSION}"

if [ `echo ${RELEASE_VERSION} | grep 'SNAPSHOT'` ]; then
  echo "Version contains SNAPSHOT. Not publishing."
  exit 0
fi


# build user-guide and javadoc
./mvnw -Ppublish-doc asciidoctor:process-asciidoc@output-html javadoc:javadoc

cd $HOME
rm -fr gh-pages

git config --global user.email "travis@travis-ci.org"
git config --global user.name "travis-ci"
git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/ttddyy/datasource-proxy gh-pages > /dev/null

cd gh-pages

# user-guide
rm -fr docs/current/user-guide
mkdir -p docs/current/user-guide
mkdir -p docs/${RELEASE_VERSION}/user-guide
cp -Rf ${TRAVIS_BUILD_DIR}/target/generated-docs/index.html docs/current/user-guide/
cp -Rf ${TRAVIS_BUILD_DIR}/target/generated-docs/index.html docs/${RELEASE_VERSION}/user-guide/

# javadoc
rm -fr docs/current/api
mkdir -p docs/current/api
mkdir -p docs/${RELEASE_VERSION}/api
cp -Rf ${TRAVIS_BUILD_DIR}/target/site/apidocs/* docs/current/api
cp -Rf ${TRAVIS_BUILD_DIR}/target/site/apidocs/* docs/${RELEASE_VERSION}/api/

git add -f .
git commit -m "Add new user-guide and javadoc for version=${RELEASE_VERSION} by travis build ${TRAVIS_BUILD_NUMBER}"
git push -fq origin gh-pages > /dev/null
