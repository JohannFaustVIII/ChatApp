#!/bin/bash

docker run --name cassandra-chat -p 9042:9042 -e CASSANDRA_KEYSPACE=chatkeyspace -d cassandra-local:latest