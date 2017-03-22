#!/bin/sh

mvn clean
mvn compile
mvn package

rm -f ./dist/challenge-1.0.0.war
cp ./target/challenge-1.0.0.war ./dist
