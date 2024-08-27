package org.chs.restdockerapis.network.util;

import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.common.exception.ErrorCode;
import org.chs.restdockerapis.network.presentation.dto.SubnetRangeDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class AddressUtils {

    public boolean validIPRangeIntoSubnet(String subnet, String ipRange) {
        String[] subnetWithCidr = subnet.split("/");
        String[] ipRangeWithCidr = ipRange.split("/");

        if (2 != subnetWithCidr.length || 2 != ipRangeWithCidr.length) {
            return false;
        }

        SubnetRangeDto subnetNetworkAddress = getSubnetRange(subnetWithCidr);
        SubnetRangeDto ipRangeNetworkAddress = getSubnetRange(ipRangeWithCidr);

        return (ipRangeNetworkAddress.startAddress() >= subnetNetworkAddress.startAddress())
                && (ipRangeNetworkAddress.endAddress() <= subnetNetworkAddress.endAddress());
    }

    public boolean validGatewayIntoSubnet(String subnet, String gateway) {
        String[] subnetWithCidr = subnet.split("/");
        if (2 != subnetWithCidr.length) {
            return false;
        }

        SubnetRangeDto subnetNetworkAddress = getSubnetRange(subnetWithCidr);

        int gatewayInt = ipToInt(gateway);

        return gatewayInt >= subnetNetworkAddress.startAddress()
                && gatewayInt <= subnetNetworkAddress.endAddress();
    }

    public boolean validAddressRangeFormat(String ipRange) {
        String[] ipWithCidr = ipRange.split("/");
        if (2 != ipWithCidr.length) {
            return false;
        }

        int cidr = -1;
        try {
            cidr = Integer.parseInt(ipWithCidr[1]);
        } catch (Exception exception) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
        }

        if (false == validAddressFormat(ipWithCidr[0]) || cidr < 0 || cidr > 32) {
            return false;
        }

        if (false == isValidSubnetStartAddress(ipWithCidr[0], cidr)) {
            return false;
        }

        return true;
    }

    public boolean validAddressFormat(String ip) {
        String[] octets = ip.split("\\.");

        if (4 != octets.length) {
            return false;
        }

        for (int octetIndex = 0; octetIndex < 4; octetIndex++) {
            int octet = -1;
            try {
                octet = Integer.parseInt(octets[octetIndex]);
            } catch (Exception exception) {
                throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
            }

            if (octet < 0 || octet > 255) {
                return false;
            }
        }

        return true;
    }

    private SubnetRangeDto getSubnetRange(String[] ipWithCidr) {
        int rangeInt = ipToInt(ipWithCidr[0]);
        int rangeCidr = Integer.parseInt(ipWithCidr[1]);

        // 마스크 계산
        int mask = -1 << (32 - rangeCidr);

        // 주소 계산
        int networkStartAddress = rangeInt & mask;
        int networkEndAddress = networkStartAddress | ~mask;

        return SubnetRangeDto.builder()
                .startAddress(networkStartAddress)
                .endAddress(networkEndAddress)
                .build();
    }

    public String automaticAllocationSubnet(List<String> subnetList) {
        int defaultCidr = 16;

        // 서브넷 리스트에서 가장 큰 CIDR을 사용한 네트워크 끝 주소를 추출
        List<SubnetRangeDto> subnetRanges = subnetList.stream()
                .map(subnet -> getSubnetRange(subnet.split("/")))
                .sorted(Comparator.comparingInt(SubnetRangeDto::endAddress))
                .toList();

        int newSubnetStart = 0;

        if (false == subnetRanges.isEmpty()) {
            // 가장 큰 서브넷의 끝 주소 다음부터 할당
            newSubnetStart = subnetRanges.get(subnetRanges.size() - 1).endAddress() + 1;
        }

        return intToIp(newSubnetStart) + "/" + defaultCidr;
    }

    public String automaticAllocationGateway(String subnet) {
        String[] subnetWithCidr = subnet.split("/");
        if (2 != subnetWithCidr.length) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
        }

        SubnetRangeDto subnetRange = getSubnetRange(subnetWithCidr);

        int gatewayInt = subnetRange.startAddress() + 1;
        return intToIp(gatewayInt);
    }

    public boolean duplicateSubnetCheck(List<String> existSubnetList, String newSubnet) {
        String[] newSubnetWithCidr = newSubnet.split("/");
        if (2 != newSubnetWithCidr.length) {
            throw new CustomBadRequestException(ErrorCode.NOT_VALID_ADDRESS_FORMAT);
        }

        SubnetRangeDto newSubnetRange = getSubnetRange(newSubnet.split("/"));

        for (String existSubnet : existSubnetList) {
            SubnetRangeDto existSubnetRange = getSubnetRange(existSubnet.split("/"));

            if (duplicateSubnetCheck(existSubnetRange, newSubnetRange)) {
                return true;
            }
        }

        return false;
    }

    private boolean duplicateSubnetCheck(SubnetRangeDto range1, SubnetRangeDto range2) {
        return range1.endAddress() >= range2.startAddress()
                && range1.startAddress() <= range2.endAddress();
    }

    /**
     * Description
     *
     * -1 = 11111111 11111111 11111111 11111111
     *
     * example
     * cidr = 24
     * -1 << (32 - cidr)
     *
     * result = 11111111 11111111 11111111 00000000
     *
     * subnetMask 와의 And 연산을 통해 CIDR 에 맞는 첫번째 주소를 가져옴
     * 192.168.1.10 & 255.255.255.0 = 192.168.1.0
     */
    private boolean isValidSubnetStartAddress(String ip, int cidr) {
        int ipInt = ipToInt(ip);
        int subnetMask = -1 << (32 - cidr);

        return (ipInt & subnetMask) == ipInt;
    }

    /**
     * Description
     *
     * << 연산을 통해 각 옥텟을 24, 16, 8, 0 비트 위치로 이동
     * |= (OR) 연산을 통해 각 옥텟을 조합해 최종 결과를 만듬
     *
     * example
     * 192 -> 192 << 24 = 11000000 00000000 00000000 00000000
     * 168 -> 168 << 16 = 00000000 10101000 00000000 00000000
     * 1 -> 1 << 8 = 00000000 00000000 00000001 00000000
     * 1 -> 1 << 0 = 00000000 00000000 00000000 00000001
     *
     * result : 11000000 10101000 00000001 00000001
     */
    private int ipToInt(String ip) {
        String[] octets = ip.split("\\.");
        int result = 0;
        for (int octetIndex = 0; octetIndex < 4; octetIndex++) {
            result |= (Integer.parseInt(octets[octetIndex]) << (24 - (8 * octetIndex)));
        }
        return result;
    }

    private String intToIp(int ipInt) {
        return String.format("%d.%d.%d.%d",
                (ipInt >> 24) & 0xFF,
                (ipInt >> 16) & 0xFF,
                (ipInt >> 8) & 0xFF,
                ipInt & 0xFF);
    }
}
