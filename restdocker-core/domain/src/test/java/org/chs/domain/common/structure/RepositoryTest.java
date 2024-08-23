package org.chs.domain.common.structure;

import org.chs.domain.common.config.TestQueryDslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

// JUnit 을 통한 테스트에서 Select 을 제외한 모든 쿼리는 Rollback 된다.
@DataJpaTest //Repository 객체를 의존주입 받을 수 있게 해주는 어노테이션
@ActiveProfiles("test")
@Import(TestQueryDslConfig.class)
public abstract class RepositoryTest {
}
