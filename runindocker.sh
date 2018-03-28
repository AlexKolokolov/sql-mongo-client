#!/bin/bash
read -p 'MongoDB host (default localhost): ' host
read -p 'MongoDB port (default 27017): ' port
read -p 'MongoDB database (default testdb): ' database

[[ -z "$host" ]] && host=localhost
[[ -z "$port" ]] && port=27017
[[ -z "$database" ]] && database=testdb

docker run -it --rm --net="host" alexkolokolov/sql-mongo-client --mongo.host=$host --mongo.port=$port --mongo.database=$database
