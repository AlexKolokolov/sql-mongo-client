## sql-mongo-client

[![Build Status](https://travis-ci.org/AlexKolokolov/sql-mongo-client.svg?branch=master)](https://travis-ci.org/AlexKolokolov/sql-mongo-client)


Interactive console MongoDB client.<br>
Consumes SQL `select` queries, converts them to MongoDB `find` queries<br> 
and runs on a MongoDB server.<br>

#### Usage

Clone the repo.<br>
Using Terminal step into the root directory of the project.

**Option 1**. If you have [Docker](https://www.docker.com/) installed on your computer:<br>
* In the root directory run `./runindocker.sh`.
The latest version of the Docker image will be downloaded from [DockerHub](https://hub.docker.com/). 
The operation may take some time on the first run.
* The application will ask you to enter `host`, `port` and `database name` of your MongoDB server. 
You can enter necessary settings or go with the defaults.<br>
* When a prompt symbol `> ` appears the application is ready to use.<br>
(May not work on MacOS because of Docker for Mac OS networking principles. Tested on Ubuntu 16.04 only)

**Option 2**. If you have `JDK 8` installed on your computer:<br>
* In the root directory run `gradlew build`. It will create executable `jar` file.
* Run `./runjar.sh`. 
* The application will ask you to enter `host`, `port` and `database name` of MongoDB server.
You can enter necessary settings or go with the defaults.
* When a prompt symbol `> ` appears the application is ready to use.