package org.chs.domain.account;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAccountLoginHistoryRepositoryImpl implements CustomAccountLoginHistoryRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
