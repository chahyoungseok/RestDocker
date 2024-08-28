package org.chs.restdockerapis.network.application.properties;

public interface DockerZeroProperties {
    String NAME = "bridge";
    String SUBNET = "172.17.0.0/16";
    String GATEWAY = "172.17.0.1";
    int MTU = 1500;
    boolean ICC = true;
}
