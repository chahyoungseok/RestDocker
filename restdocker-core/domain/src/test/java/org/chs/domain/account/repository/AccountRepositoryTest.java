package org.chs.domain.account.repository;

import org.chs.domain.account.AccountRepository;
import org.chs.domain.account.entity.AccountEntity;
import org.chs.domain.common.RepositoryTest;
import org.chs.domain.common.enumerate.ThirdPartyEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRepositoryTest extends RepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    AccountEntity account1;

    @BeforeEach
    void beforeAll() {
        account1 = AccountEntity.builder()
                .nickname("테스트용 계정1")
                .oauthServiceId("test_account_1")
                .thirdPartyType(ThirdPartyEnum.KAKAO)
                .isActive(true)
                .build();

        account1.setDateTimeForTest(LocalDateTime.now(), LocalDateTime.now());
        accountRepository.save(account1);
    }

    @Test
    @DisplayName("고유_OAuthServiceId와_ThirdParty로_계정을_조회한다")
    void findByOauthServiceIdEqualsAndThirdPartyTypeEquals_메서드를_테스트한다() {
        // given
        String testOAuthServiceId = "test_account_1";
        ThirdPartyEnum testThirdPartyType = ThirdPartyEnum.KAKAO;

        // when
        Optional<AccountEntity> testSelectResult = accountRepository
                .findByOauthServiceIdEqualsAndThirdPartyTypeEquals(testOAuthServiceId, testThirdPartyType);

        // then
        assertThat(testSelectResult.get().getOauthServiceId()).isEqualTo(account1.getOauthServiceId());
    }

}
