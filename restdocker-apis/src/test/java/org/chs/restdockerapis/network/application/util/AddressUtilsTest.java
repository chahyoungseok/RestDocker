package org.chs.restdockerapis.network.application.util;

import org.chs.restdockerapis.common.exception.CustomBadRequestException;
import org.chs.restdockerapis.network.util.AddressUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AddressUtilsTest {

    @InjectMocks
    private AddressUtils addressUtils;

    @Nested
    @DisplayName("[Network][시나리오 테스트] Subnet 범위안에 IPRange가 있는지 테스트한다.")
    class ValidIPRangeIntoSubnet {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] Subnet 범위안에 IPRange가 있어 성공한다")
        void Subnet_범위안에_IPRange가_있어_성공한다() {
            // given - data
            String subnet = "172.17.0.0/16";
            String ipRange = "172.17.0.0/24";

            // when
            boolean actual = addressUtils.validIPRangeIntoSubnet(subnet, ipRange);

            // then
            Assertions.assertEquals(true, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] Subnet 범위안에 IPRange가 없어 실패한다.")
        void Subnet_범위안에_IPRange가_없어_실패한다() {
            // given - data
            String subnet = "172.17.0.0/16";
            String ipRange = "172.18.0.0/24";

            // when
            boolean actual = addressUtils.validIPRangeIntoSubnet(subnet, ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] Subnet 범위안에 Gateway가 있는지 테스트한다.")
    class ValidGatewayIntoSubnet {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] Subnet 범위안에 Gateway가 있어 성공한다")
        void Subnet_범위안에_Gateway가_있어_성공한다() {
            // given - data
            String subnet = "172.17.0.0/16";
            String ipRange = "172.17.255.1";

            // when
            boolean actual = addressUtils.validGatewayIntoSubnet(subnet, ipRange);

            // then
            Assertions.assertEquals(true, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] Subnet 범위안에 Gateway가 없어 실패한다.")
        void Subnet_범위안에_Gateway가_없어_실패한다() {
            // given - data
            String subnet = "172.17.0.0/16";
            String ipRange = "172.16.255.255";

            // when
            boolean actual = addressUtils.validGatewayIntoSubnet(subnet, ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 주소대역 형식이 유효한지 테스트한다.")
    class ValidAddressRangeFormat {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 주소대역 형식이 유효하여 성공한다")
        void 주소대역_형식이_유효하여_성공한다() {
            // given - data
            String ipRange = "172.17.0.0/16";

            // when
            boolean actual = addressUtils.validAddressRangeFormat(ipRange);

            // then
            Assertions.assertEquals(true, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] CIDR 값이 유효하지않아 실패한다.")
        void CIDR_값이_유효하지않아_실패한다() {
            // given - data
            String ipRange = "172.17.0.0/33";

            // when
            boolean actual = addressUtils.validAddressRangeFormat(ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 옥텟값이 유효하지않아 실패한다.")
        void 옥텟값이_유효하지않아_실패한다() {
            // given - data
            String ipRange = "172.256.0.0/16";

            // when
            boolean actual = addressUtils.validAddressRangeFormat(ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] CIDR에 따른 대역의 시작주소가 아니어서 실패한다.")
        void CIDR에_따른_대역의_시작주소가_아니어서_실패한다() {
            // given - data
            String ipRange = "172.17.0.1/16";

            // when
            boolean actual = addressUtils.validAddressRangeFormat(ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 주소 형식이 유효한지 테스트한다.")
    class ValidAddressFormat {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 주소 형식이 유효하여 성공한다")
        void 주소대역_형식이_유효하여_성공한다() {
            // given - data
            String ipRange = "172.17.0.0";

            // when
            boolean actual = addressUtils.validAddressFormat(ipRange);

            // then
            Assertions.assertEquals(true, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 옥텟값이 유효하지않아 실패한다.")
        void 옥텟값이_유효하지않아_실패한다() {
            // given - data
            String ipRange = "172.255.256.0";

            // when
            boolean actual = addressUtils.validAddressFormat(ipRange);

            // then
            Assertions.assertEquals(false, actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 옥텟값을 파싱하는 과정에서 실패한다.")
        void 옥텟값을_파싱하는_과정에서_실패한다() {
            // given - data
            String ipRange = "172.가.나.다";

            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> addressUtils.validAddressFormat(ipRange)
            );
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 현재 존재하는 서브넷들을 피해 서브넷을 자동 할당한다.")
    class AutomaticAllocationSubnet {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 서브넷 자동할당을 성공한다")
        void 서브넷_자동할당을_성공한다() {
            // given - data
            List<String> subnetList = List.of("172.17.0.0/16", "128.25.0.0/16", "192.168.14.0/24");

            // when
            String actual = addressUtils.automaticAllocationSubnet(subnetList);

            // then
            Assertions.assertEquals("192.169.0.0/16", actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] IP 대역이 유효하지않아 실패한다.")
        void IP_대역이_유효하지않아_실패한다() {
            // given - data
            List<String> subnetList = List.of("172.17.0.0|16", "128.25.0.0/16", "192.168.14.0/24");

            // when & then
            Assertions.assertThrows(
                    NumberFormatException.class,
                    () -> addressUtils.automaticAllocationSubnet(subnetList)
            );
        }
    }

    @Nested
    @DisplayName("[Network][시나리오 테스트] 서브넷대역 안에 게이트웨이를 자동 할당한다.")
    class AutomaticAllocationGateway {

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] 서브넷대역안에 게이트웨이 자동할당을 성공한다")
        void 서브넷대역안에_게이트웨이_자동할당을_성공한다() {
            // given - data
            String ipRange = "172.17.0.0/16";

            // when
            String actual = addressUtils.automaticAllocationGateway(ipRange);

            // then
            Assertions.assertEquals("172.17.0.1", actual);
        }

        @Tag("business")
        @Test
        @DisplayName("[Network][Business] IP 대역이 유효하지않아 실패한다.")
        void IP_대역이_유효하지않아_실패한다() {
            // given - data
            String ipRange = "172.17.0.0|16";

            // when & then
            Assertions.assertThrows(
                    CustomBadRequestException.class,
                    () -> addressUtils.automaticAllocationGateway(ipRange)
            );
        }
    }
}
