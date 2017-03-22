#!/bin/sh

export CDIR=$PWD

if [ ! -z "$1" ];
then
	echo "Command argument is set to ${1}, using that storage config";
	export INIT_STORAGE_PATH=$1
elif [ ! -z "$STORAGE_PATH" ];
then
	echo "Env var STORAGE_PATH set to ${STORAGE_PATH}, using that storage config";
	export INIT_STORAGE_PATH=$STORAGE_PATH
else
	echo "Neither env var STORAGE_PATH or command argument was found, initializing in parent of current folder";
	export INIT_STORAGE_PATH=${CDIR}
fi

# java -jar ${PWD}/challenge/dist/challenge-1.0.0.war --com.mesosphere.challenge.service.storage.path=${INIT_STORAGE_PATH}
java -jar ${PWD}/challenge/dist/challenge-1.0.0.war --com.mesosphere.challenge.service.storage.path=${INIT_STORAGE_PATH} --com.mesosphere.challenge.service.storage.file=challengedb
