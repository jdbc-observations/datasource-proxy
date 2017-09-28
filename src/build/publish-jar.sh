#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" == 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_d6173cb2f134_key -iv $encrypted_d6173cb2f134_iv -in src/build/codesigning.asc.enc -out target/codesigning.asc -d
    gpg --fast-import target/codesigning.asc

    ./mvnw deploy --settings src/build/settings.xml -DskipTests=true
fi


