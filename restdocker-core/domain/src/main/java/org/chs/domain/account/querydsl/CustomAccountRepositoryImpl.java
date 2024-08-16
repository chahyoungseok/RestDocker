package org.chs.domain.account.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAccountRepositoryImpl implements CustomAccountRepository {

    private JPAQueryFactory jpaQueryFactory;
}
