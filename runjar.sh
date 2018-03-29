#!/bin/bash
read -p 'MongoDB host (default localhost): ' host
read -p 'MongoDB port (default 27017): ' port
read -p 'MongoDB database (default devdb): ' database

[[ -z "$host" ]] && host=localhost
[[ -z "$port" ]] && port=27017
[[ -z "$database" ]] && database=devdb

java -jar build/libs/sql-mongo-client.jar --mongo.host=$host --mongo.port=$port --mongo.database=$database
