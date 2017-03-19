#!/usr/bin/env bash

if [ $# -gt 0 ]; then
    echo "Your command line contains $# arguments"
else
    echo "Your command line contains no arguments"
fi

mkdir -p pa3_test/test1
mvn clean
mvn package
cp target/File*.jar pa3_test/test1
java -jar pa3_test/test1/FileSharingSystem-jar-with-dependencies.jar
