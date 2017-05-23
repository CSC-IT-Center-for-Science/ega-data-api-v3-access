#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sudo docker run --rm --name build -v $DIR:/EGA_build -it alexandersenf/ega_build sh -c 'exec /EGA_build/build.sh'
sudo docker build -t ega_access -f Dockerfile_Deploy .
sudo rm Access-0.0.1-SNAPSHOT.war
sudo rm Dockerfile_Deploy
sudo rm accessd.sh
sudo docker run -d -p 9051:9051 ega_access
