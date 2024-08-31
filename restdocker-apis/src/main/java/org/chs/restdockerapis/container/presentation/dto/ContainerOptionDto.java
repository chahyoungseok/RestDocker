package org.chs.restdockerapis.container.presentation.dto;

import lombok.Getter;
import org.chs.restdockerapis.container.application.ContainerArgProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContainerOptionDto {

    private String portForward;

    private String name;

    private String imageFullName;

    private boolean isRm;

    private String networkName;

    private String containerIp;

    private List<String> options = new ArrayList<>();

    public void putArgCommand(String[] args) {

        switch (args[0]) {
            case ContainerArgProperties.NAME -> this.name = args[1];
            case ContainerArgProperties.RM -> this.isRm = true;
            case ContainerArgProperties.NETWORK -> this.networkName = args[1];
            case ContainerArgProperties.IP -> this.containerIp = args[1];
            case ContainerArgProperties.PORT -> this.portForward = args[1];
        }
    }

    public boolean validNotExistNetwork() {
        if (null == this.networkName && null != this.containerIp) {
            return false;
        }

        return true;
    }

    public void setImageFullName(String imageFullName) {
        this.imageFullName = imageFullName;
    }

    public void setBridgeNetwork() {
        this.networkName = "bridge";
    }

    public void setContainerIp(String containerIp) {
        this.containerIp = containerIp;
    }
}
