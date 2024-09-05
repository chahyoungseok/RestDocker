#!/bin/bash

. /opt/bin/scripts/restdocker-package-info.sh

CONTAINER=`docker ps -a | grep ${RESTDOCKER_CONTAINER_NAME}`

if [ -z "${CONTAINER}" ]; then
  docker rm ${RESTDOCKER_CONTAINER_NAME} 2> /dev/null
fi

docker run --name ${RESTDOCKER_CONTAINER_NAME} \
	   --rm \
	   -itd \
	   --net ${RESTDOCKER_DOCKER_NETWORK_NAME} \
	   --ip ${RESTDOCKER_DOCKER_NETWORK_IP} \
	   -p 443:8080 \
	   -e SPRING_PROFILES_ACTIVE=prod \
	   -e TZ=Asia/Seoul \
	   -v /opt/serverlog/restdocker:/var/log/common/info \
	   ${RESTDOCKER_CONTAINER_NAME}:${RESTDOCKER_DOCKER_VERSION}
