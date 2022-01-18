#!/bin/bash

BASEDIR=$(dirname "$0")
INDEX_FILE="${BASEDIR}/../index.md"

BRANCH=$1
if [[ "${BRANCH}" == "" ]]
	then
		BRANCH="main"
fi

git checkout $BRANCH "${BASEDIR}/../LICENSE"
git checkout $BRANCH "${BASEDIR}/../Plugins/Gradle/README.md"
git checkout $BRANCH "${BASEDIR}/../README.md"
git checkout $BRANCH "${BASEDIR}/../RECIPES.md"

echo "---" > $INDEX_FILE
echo "layout: index" >> $INDEX_FILE
echo "---" >> $INDEX_FILE
echo "" >> $INDEX_FILE

cat "${BASEDIR}/../README.md" >> $INDEX_FILE