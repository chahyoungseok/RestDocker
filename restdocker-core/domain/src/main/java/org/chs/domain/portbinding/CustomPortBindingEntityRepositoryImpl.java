package org.chs.domain.portbinding;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPortBindingEntityRepositoryImpl implements CustomPortBindingEntityRepository{

    private final JPAQueryFactory jpaQueryFactory;
}
