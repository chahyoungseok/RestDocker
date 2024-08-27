package org.chs.restdockerapis.network.presentation.dto;

import lombok.Getter;
import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.network.application.properties.NetworkArgProperties;
import org.chs.restdockerapis.network.application.properties.NetworkOptionProperties;

@Getter
public class NetworkOptionDto {
    private String name;
    private String subnet;
    private String ipRange;
    private String gateway;
    private Integer mtu;
    private Boolean icc;

    public void putArgCommand(String[] args) {

        switch (args[0]) {
            case NetworkArgProperties.SUBNET -> this.subnet = args[1];
            case NetworkArgProperties.IPRANGE -> this.ipRange = args[1];
            case NetworkArgProperties.GATEWAY -> this.gateway = args[1];
            case NetworkArgProperties.OPTION -> {
                String[] networkOption = args[1].split("=");
                if (2 != networkOption.length) {
                    throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
                }

                switch (networkOption[0]) {
                    case NetworkOptionProperties.ICC -> this.icc = Boolean.parseBoolean(networkOption[1]);
                    case NetworkOptionProperties.MTU -> this.mtu = Integer.valueOf(networkOption[1]);
                    default -> throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
                }
            }
            default -> throw new CustomBadRequestException(ErrorCode.ARGUMENT_COMMAND_NOT_VALID_EXCEPTION);
        }
    }

    public void setAutomaticAddress(String subnet, String gateway) {
        this.subnet = subnet;
        this.gateway = gateway;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean validNotExistSubnet() {
        if (null == this.subnet) {
            return null == this.ipRange && null == this.gateway;
        }

        return true;
    }
}
