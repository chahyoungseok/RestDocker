#!/bin/bash

docker network create \
        --subnet 172.200.0.0/16 \
        --gateway 172.200.1.0 \
        --opt com.docker.network.bridge.enable_icc=true \
        --opt com.docker.network.driver.mtu=1500 \
        restdocker_network