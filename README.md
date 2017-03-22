


Introduction
==============

This is an implementation of the Mesosphere challenge here:
https://gist.github.com/sargun/fad3b836debc1472b397ff441ccb014f

This implementation is written in java, and uses the Spring framework with Jackson for REST operations, 
and Spring boot for launching the service.

There are prebuilt Docker and Docker compose files for a seamless container launch experience.



Prerequisites
==============

To build this project, you need the following:

- java 1.8
- maven 3 (>=3.2)
- docker
- docker-compose

To run, you just need a java 8 install and docker.



Build
==============

> ./build.sh
This will build the project and install the challenge-1.0.0.war executable WAR into the dist/ folder.

> docker-compose build
This will build the docker container on top of an openjdk:8 image found in the docker repository

> docker-compose up
This will launch the docker container, and boot the challenge WAR file.

The build tries its best to figure out where to host the embedded DB file(s), however if it fails, 
you can override this behavior by setting $STORAGE_PATH as an environment variable and passing into
challenge/bin/challenge-executable.sh in the Dockerfile.

There are two dockerfiles included, the first one, the default Dockerfile, uses openjdk:8 as a base image
however the requirements stated runnign the project on ubuntu:16.04. Therefore another dockerfile is included.
This file, Dockerfile.ubuntu, uses a vanilla ubuntu:16.04 base image and installs the java requirements into
the build container. Note that this process is much slower as it relies on updating the ubuntu base system
via apt, so I suggest running with the default openjdk:8 dockerfile.

The last step, docker-compose up, boots the docker image, and after booting, the challenge REST API is reacheable on :7777.



Running
==============

If you are interested in running the API outside of the standard build and boot process, you can easily boot
the API like this:

> java -jar path/to/dist/challenge-1.0.0.war --com.mesosphere.challenge.service.storage.path=/path/to/where/you/want/your/embedded/db --com.mesosphere.challenge.service.storage.file=challengedb

The embedded DB does not get deleted upon shutting down or restarting, so you can easily Ctrl-C the process and start 
it again with the same command.



Tests
==============

There are 2 unit test classes, provided with the project. Each unit test class runs the same, or similar, set of tests
and the main difference between them is the backing storage. The two classes are:

- com.mesosphere.challenge.service.test.InMemoryStorageControllerTest
- com.mesosphere.challenge.service.test.EmbeddedStorageControllerTest

The InMemoryStorageControllerTest uses a simple HashMap<> in memory backing storage provided via the 
com.mesosphere.challenge.service.dao.MemoryStorageDAO DAO class.

The EmbeddedStorageControllerTest uses a full SQL in memory database provided via the 
com.mesosphere.challenge.service.dao.EmbeddedStorageDAO DAO class.

To run the unit tests, you can either run them via your IDE (like Eclipse) or via maven like this:

> mvn test



Code layout
==============

- ./main/java/com/mesosphere/challenge/model/StorageModel.java - Base JSON model definition.
- ./main/java/com/mesosphere/challenge/model/StorageNode.java - Model representing storage blob, has fields relevant for file in storage.

- ./main/java/com/mesosphere/challenge/service/Application.java - Main Spring boot application, has main method.

- ./main/java/com/mesosphere/challenge/service/config/StorageServiceConfig.java - Part of Spring framework config.

- ./main/java/com/mesosphere/challenge/service/controller/StorageController.java - Main challenge API REST controller.

- ./main/java/com/mesosphere/challenge/service/dao/EmbeddedStorageDAO.java - DAO that provides embedded DB storage, both in memory and file backed.
- ./main/java/com/mesosphere/challenge/service/dao/FileStorageDAO.java - DAO that provides file system based storage. This class is not used in the examples.
- ./main/java/com/mesosphere/challenge/service/dao/IStorageDAO.java - DAO interface that all DAOs extend.
- ./main/java/com/mesosphere/challenge/service/dao/MemoryStorageDAO.java - In memory HashMap<> backed storage for simple unit test mocking.

- ./main/java/com/mesosphere/challenge/StorageException.java - Main challenge API checked exception class. Thrown by all API methods that throw exceptions, and wraps all underlying exceptions.

- ./test/java/com/mesosphere/challenge/service/test/EmbeddedStorageControllerTest.java - Embedded DB unit test suite.
- ./test/java/com/mesosphere/challenge/service/test/InMemoryStorageControllerTest.java - In memory unit test suite.
- ./test/java/com/mesosphere/challenge/service/test/StorageTestConfig.java - Part of Spring framework config.
