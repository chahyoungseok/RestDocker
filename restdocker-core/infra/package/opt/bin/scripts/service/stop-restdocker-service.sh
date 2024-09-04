#!/bin/bash

. /opt/bin/scripts/restdocker-package-info.sh

docker stop ${RESTDOCKER_CONTAINER_NAME}
