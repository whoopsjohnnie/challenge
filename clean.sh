#!/bin/sh

find . -name '*.DS_Store' -type f -delete
# find . -name ".DS_Store" -depth -exec rm {} \;

mvn clean

rm -rf ./target/
