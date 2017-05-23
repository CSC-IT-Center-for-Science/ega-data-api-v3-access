#!/bin/bash
git clone https://github.com/elixir-europe/ega-data-api-v3-access.git
mvn -f /ega-data-api-v3-access/pom.xml install
mv /ega-data-api-v3-access/target/Access-0.0.1-SNAPSHOT.war /EGA_build
mv /ega-data-api-v3-access/docker/accessd.sh /EGA_build
mv /ega-data-api-v3-access/docker/Dockerfile_Deploy /EGA_build
